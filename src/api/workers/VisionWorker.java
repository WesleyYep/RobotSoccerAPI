package api.workers;

import api.data.BoardProperties;
import api.data.Point;
import api.data.VisionParameters;
import api.robots.Robots;
import api.vision.LookupTable;
import api.vision.Patch;
import api.vision.Position;
import api.vision.SegmentCount;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Arrays;

public class VisionWorker extends Worker {
	private int scanInterval = 26;
    private byte[] pMarkTable = new byte[640*480];
    private ArrayList<Patch> teamPatchList = new ArrayList<Patch>();
	private ArrayList<Patch> ballPatchList = new ArrayList<Patch>();
	private ArrayList<Patch> enemyPatchList = new ArrayList<Patch>();
    private ArrayList<SegmentCount> segmentCountList = new ArrayList<SegmentCount>();
    private int NEXT_Y;
    private VisionParameters visionParameters;
    private int valueSegmentCheckDistance = 1;
    private int valueSegmentThreshold = 2;
    private Point[] segmentPosition;
    private int[][] segCombination = new int[11][4];
    private Position[] robotHome = new Position[5];
    public static final int MAX_ROBOT = 5;
	private ArrayList<Point> segmentPointList = new ArrayList<Point>();
	private boolean[][] bFound = new boolean[5][100];
	private int testCount = 0;

    public VisionWorker(VisionParameters vp, Robots robots, Robots opponents) {
		super(robots, opponents);
		this.visionParameters = vp;

		segmentPosition = new Point[4];
		segmentPosition[0] = new Point(-7.5/3, 7.5/4);
		segmentPosition[1] = new Point(7.5/3, 7.5/4);
		segmentPosition[2] = new Point(-7.5/3, -7.5/4);
		segmentPosition[3] = new Point(7.5/3, -7.5/4);
		
		for (int i = 0; i<5; i++) {
			robotHome[i] = new Position();
		}
		
	}

	@Override
	public void beginProcessing() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Load the native OpenCV library
				System.out.println(System.getProperty("java.library.path"));
				System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

				VideoCapture videoCapture = new VideoCapture(0);
				Mat image = new Mat();

				if(!videoCapture.isOpened()) {
					System.out.println("cannot open camera");
					return;
				}

				videoCapture.read(image);
				if(image.empty()){
					System.out.println("ERR: Unable to query image from capture device.");
					return;
				}
				while (true) {
					videoCapture.read(image);
					try {
						Imgproc.GaussianBlur(image, image, new org.opencv.core.Size(5, 5), 0, 0);
						Run_InitFlags();
						Run_SearchPatch(image);
						Run_FindPatchPosition(teamPatchList);
						Run_FindBall(image);
						Run_FindRobot(image);
						Run_FindOpponent();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

        thread.start();
	}

	public void Run_InitFlags() {
		teamPatchList.clear();
		ballPatchList.clear();
		enemyPatchList.clear();
		segmentCountList.clear();
		segmentPointList.clear();
		Arrays.fill(pMarkTable, (byte)0);
		
		int[][] matching_data = new int[][] {
				{1,0,0,0},
				{0,1,0,0},
				{1,1,0,0},
				{1,1,1,0},
				{1,1,0,1}
		};
		
		for (int id =0; id<MAX_ROBOT; id++) {
			for (int seg=0; seg<4; seg++) {
				segCombination[id][seg] = matching_data[id][seg];
			}
		}
	}

    public void Run_SearchPatch(Mat image) {
        // Full range HSV. Range 0-255.
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV_FULL);
		int imageWidth = image.width();
		int imageHeight = image.height();
        int p = 0;
        int NEXT_X = scanInterval*3;
        NEXT_Y = imageWidth*3;

        int total = (imageWidth*imageHeight/scanInterval);
        while ( (total -= 1) > 0) {
            int x = (p/3)%imageWidth;
            int y =  p/imageWidth/3;

			if( x > 0	&& y > 0  && x < imageWidth-1 && y < imageHeight -1) {
            }
            else {
                p+= NEXT_X;
                continue;
            }

			//TODO - limit processing area
			//	if (VisionController.processingArea[x+y*640] == 1) return;

            double[] hsv = image.get(y,x);
			//System.out.println(p + " " + y + " " + x + " " + total );
			byte patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
			//System.out.println(x + " " + y +  " " + hsv[0] + " " + hsv[1] + " " + hsv[2]);

            if ( (patchLUTData & LookupTable.TEAM_COLOUR) > 0 ) {
                FindPatch(p,x,y,image,LookupTable.TEAM_COLOUR,1,teamPatchList,visionParameters.robotSizeMax, visionParameters.robotSizeMin);
            }
			if ( (patchLUTData & LookupTable.BALL_COLOUR)  > 0 ) {
				FindPatch(p,x,y,image,LookupTable.BALL_COLOUR,1,ballPatchList,visionParameters.ballSizeMin,visionParameters.ballSizeMax);
			}
			if ( (patchLUTData & LookupTable.OPPONENT_COLOUR)  > 0 ) {
				FindPatch(p,x,y,image,LookupTable.OPPONENT_COLOUR,1,enemyPatchList,visionParameters.opponentSizeMin,visionParameters.opponentSizeMax);
            }
			
			p = p + NEXT_X;
        }
    }

    private void FindPatch(int p, int x, int y, Mat image, byte mask, int scanInterval, ArrayList<Patch> patchList, int valueMin, int valueMax) {
        if (! (pMarkTable[p/3] == mask)) {
        	Patch patch = new Patch();
	        SearchPathRecursive(p,x,y, patch, image, mask, scanInterval);

	        if  (valueMin <= patch.pixels.size() && patch.pixels.size() <= valueMax) {
	            Patch patchFilter = new Patch();
	            Point[] pointArray = new Point[4];
	            byte patchLUTData;
	            int check_neighbor;
	
	            for (Point it : patch.pixels) {
	            	if (it.x > 0+scanInterval && it.x < image.width() - scanInterval && it.y > 0+scanInterval && it.y < image.height()-scanInterval) {
	            		pointArray[0] = new Point (it.x + -scanInterval, it.y);
	            		pointArray[1] = new Point (it.x + scanInterval, it.y);
	            		pointArray[2] = new Point (it.x, it.y-scanInterval);
	            		pointArray[3] = new Point( it.x, it.y+scanInterval);
	            		check_neighbor = 0;
	            		
	            		for (int i = 0; i<4; i++) {
	            			double[] hsv = image.get(y, x);
							patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
			
							if( (patchLUTData & mask) > 0 ) {
								check_neighbor++;
							}
	            		}
	            		if (check_neighbor > 2) patchFilter.pixels.add(it);
	            	}
	            }
	            
	            if (valueMin <= patchFilter.pixels.size() && patchFilter.pixels.size() <= valueMax) {
	            	patchList.add(patchFilter);
	            }
	        }
        }
    }

    private void SearchPathRecursive(int p, int x, int y, Patch patch, Mat image, byte mask, int scanInterval) {
        int q = p/3;
        patch.pixels.add(new Point(x,y));
		//System.out.println(x + " " + y);
        if (patch.pixels.size() < visionParameters.robotSizeMax) {
            if (scanInterval > 1) {
                for (int i = 0; i<scanInterval ;i++) {
                    for (int j=0; j<scanInterval; j++) {

                        if (x+1<640 && y+j < 480) pMarkTable[(x+i) + (y+j)*image.width()]  |= mask;
                    }
                }
            } else {
                pMarkTable[q] |= mask;
            }

            byte patchLUTData;
            //LEFT
            if ( x > 0 && !((pMarkTable[q-scanInterval] & mask) > 0) ){
                double[] hsv = image.get(y,x-scanInterval);
                patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
                if((patchLUTData & mask) > 0) SearchPathRecursive(p-3*scanInterval, x-scanInterval, y,patch,image,mask,1);
            }
            //UP
            if (y > 0 && !((pMarkTable[q-scanInterval*image.width()] & mask) > 0) ) {
                double[] hsv = image.get(y-scanInterval,x);
				patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
                if((patchLUTData & mask) > 0) SearchPathRecursive(p-NEXT_Y*scanInterval, x, y-scanInterval,patch,image,mask,1);
            }
            //RIGHT
            if (x < image.width() && !((pMarkTable[q+scanInterval] & mask) > 0) ) {
                double[] hsv = image.get(y,x+scanInterval);
				patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
                if ((patchLUTData & mask) > 0)  SearchPathRecursive(p+3*scanInterval, x+scanInterval, y,patch,image,mask,1);
            }

            //DOWN
            if (y < image.height() && !((pMarkTable[q+scanInterval*image.width()] & mask) > 0) ) {
                double[] hsv = image.get(y+scanInterval,x);
				patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);
                if ( (patchLUTData & mask) > 0) SearchPathRecursive(p+NEXT_Y*scanInterval,x,y+scanInterval,patch,image,mask,1);
            }
        }
    }
    
    private void Run_FindPatchPosition(ArrayList<Patch> patchList) {
    	for (int i = 0; i<patchList.size(); i++) {
    		double sumX = 0;
    		double sumY = 0;
    		for (int p = 0; p<patchList.get(i).pixels.size(); p++) {
    			sumX += patchList.get(i).pixels.get(p).x;
    			sumY += patchList.get(i).pixels.get(p).y;
    		}
    		
    		patchList.get(i).found = true;
    		
    		//pixel Position  		
    		patchList.get(i).center.x = sumX/patchList.get(i).pixels.size();
    		patchList.get(i).center.y = sumY/patchList.get(i).pixels.size();
    		
    		//calc real position
    		Point temp = BoardProperties.imagePosToActualPos(new Point(patchList.get(i).center.x,patchList.get(i).center.y));
			//Point ground = VisionController.ScreenToGround(revision);
    		patchList.get(i).realCenter.x = temp.x/100.00;
    		patchList.get(i).realCenter.y = (180.00 - temp.y)/100.00;

			//System.out.println("i: "  + i);
			//System.out.println("real center: " + temp.x + " " + (180-temp.y));
    	}
	}
    
    public void Run_FindRobot(Mat image) {
    	byte maskTeam = LookupTable.TEAM_COLOUR;
    	byte maskBlack = LookupTable.GROUND_COLOUR;
    	//need to replace with black in look up table *reminder for myself
		//System.out.println("team patch list size: " + teamPatchList.size());
        for (int i =0; i<teamPatchList.size(); i++) {
			//System.out.println("i :"  + i);
            int[] segment_count = new int[4];
            segment_count[0] = 0;
            segment_count[1] = 0;
            segment_count[2] = 0;
            segment_count[3] = 0;

            double[] robot_angle = new double[2];
            double[] robot_angle_screen = new double[2];
            double a = 0, b = 0, c = 0;

            for (int p = 0; p<teamPatchList.get(i).pixels.size(); p++) {
                a += (teamPatchList.get(i).pixels.get(p).x - teamPatchList.get(i).center.x)*(teamPatchList.get(i).pixels.get(p).x - teamPatchList.get(i).center.x);
                b += (teamPatchList.get(i).pixels.get(p).x - teamPatchList.get(i).center.x)*(teamPatchList.get(i).pixels.get(p).y - teamPatchList.get(i).center.y);
                c += (teamPatchList.get(i).pixels.get(p).y - teamPatchList.get(i).center.y)*(teamPatchList.get(i).pixels.get(p).y - teamPatchList.get(i).center.y);
            }
            //if (i == 1) System.out.println("a: " + a + " b: " + b + " c: " + c);
            double angle_rad = Math.atan2(b, a-c)/2;
            double angle_vision_degree = angle_rad*180/Math.PI;

            while (angle_vision_degree > 180) angle_vision_degree -= 360;
            while (angle_vision_degree < -180) angle_vision_degree += 360;

            double k1 = -angle_vision_degree*4;
            double k2 = Math.sin(k1/180*Math.PI)*10;
            angle_rad = angle_rad - k2*Math.PI/180;
            //if(i==1) System.out.println("angle_rad " + angle_rad);
            Point anglePoint = new Point(0,0);
            anglePoint.x = teamPatchList.get(i).center.x + 0.1*Math.cos(angle_rad);
            anglePoint.y = teamPatchList.get(i).center.y + 0.1*Math.sin(angle_rad);

            double rasX = anglePoint.x - teamPatchList.get(i).center.x;
            double rasY = anglePoint.y - teamPatchList.get(i).center.y;
            robot_angle_screen[0] = Math.atan2(rasY,rasX);
            while (robot_angle_screen[0] < -Math.PI) robot_angle_screen[0] += (2*Math.PI);
            while (robot_angle_screen[0] > Math.PI) robot_angle_screen[0] -= (2*Math.PI);
            robot_angle_screen[1] = robot_angle_screen[0] + Math.PI;

            Point tempPoint = BoardProperties.imagePosToActualPos(anglePoint);
            double raX = tempPoint.x/100.00 - teamPatchList.get(i).realCenter.x;
            double raY = (180-tempPoint.y)/100.00 - teamPatchList.get(i).realCenter.y;
            robot_angle[0] = Math.atan2(raY,raX);
            while (robot_angle[0] < -Math.PI) robot_angle[0] += (2*Math.PI);
            while (robot_angle[0] > Math.PI) robot_angle[0] -= (2*Math.PI);
            robot_angle[1] = robot_angle[0] + Math.PI;

            double RadAngle = robot_angle[0] - Math.PI/2;
            double cx = teamPatchList.get(i).realCenter.x;
            double cy = teamPatchList.get(i).realCenter.y;
            double rad = RadAngle;
            double cosTheta = Math.cos(rad);
            double sinTheta = Math.sin(rad);

            for (int s =0; s<4; s++) {
                double d = valueSegmentCheckDistance /1000.00;
                double[] dx = {0, -d, -d, d, d};
                double[] dy = {0, -d, d, -d, d};
                for (int p=0; p<5; p++) {
                    double x = segmentPosition[s].x/100.0 + dx[p];
                    double y = segmentPosition[s].y/100.0 + dy[p];
                    double seg_x = cx + cosTheta*x - sinTheta*y;
                    double seg_y = cy + sinTheta*x + cosTheta*y;
                    Point temp = BoardProperties.actualPosToimagePos(new Point(seg_x*100,180-(seg_y*100)));
                    double k = temp.x;
                    double j = temp.y;
                    double[] hsv = image.get((int)j, (int)k);
                    segmentPointList.add(new Point(k,j));
                    if (hsv != null) {
                        byte patchLUTData = hsv == null ? 0 : LookupTable.getLUTData((int)hsv[0],(int)hsv[1],(int)hsv[2]);

                        if ((patchLUTData & maskBlack) > 0) {

                        } else {
                            segment_count[s]++;
                        }
                    }
                }
            }

            SegmentCount new_segment_count = new SegmentCount();
            new_segment_count.team_patch_id = i;
    		new_segment_count.inverseOrientation = false;
    		new_segment_count.orientation_rad    = robot_angle[0];
    		new_segment_count.orientation_screen = robot_angle_screen[0];

    		for( int s=0 ; s<4 ; s++ ) {
    			new_segment_count.count[s] = segment_count[s];
    		}
    		segmentCountList.add(new_segment_count);
    		SegmentCount new_segment_count2 = new SegmentCount();
    		new_segment_count2.team_patch_id = i;
    		new_segment_count2.inverseOrientation = true;
    		new_segment_count2.orientation_rad    = robot_angle[1];
    		new_segment_count2.orientation_screen = robot_angle_screen[1];
    		
    		for( int s=0 ; s<4 ; s++ ) {
    			new_segment_count2.count[3-s] = segment_count[s];

    		}
    		segmentCountList.add(new_segment_count2);
        }


        int[] best_robot_seg_id = new int[MAX_ROBOT];
        
        for( int i=0 ; i<MAX_ROBOT ; i++ ) {
    		best_robot_seg_id[i] = -1;
    	}

        for (int i = 0; i<segmentCountList.size(); i++) {
        	int match_id = -1;
        	
        	for (int id = 0; id<MAX_ROBOT; id++) {
        		int match = 0;
        		
        		for (int s =0; s<4; s++) {
        			if (segCombination[id][s] == -1) {
        				match++;
        			} else {
        				if (segmentCountList.get(i).count[s] < valueSegmentThreshold && segCombination[id][s] == 0) {
        					match++;
        				} 
        				if (segmentCountList.get(i).count[s] >= valueSegmentThreshold && segCombination[id][s] == 1) {
        					match++;
        				} 
        			}
        		}
        		if (match == 4) {
        			match_id = id;
        		}
        	}
        	
        	if (match_id >= 0) {
        		if (best_robot_seg_id[match_id] == -1) {
        			best_robot_seg_id[match_id] = i;
        		} else {
        			int team_patch_i_best = segmentCountList.get(best_robot_seg_id[match_id]).team_patch_id;
        			int team_patch_i = segmentCountList.get(i).team_patch_id;
        			
        			if (teamPatchList.get(team_patch_i).pixels.size() > teamPatchList.get(team_patch_i_best).pixels.size()) {
        				best_robot_seg_id[match_id] = i;
        			}
        		}
        	}
        }
        for( int id=0 ; id<MAX_ROBOT ; id++ ) {
    		if( best_robot_seg_id[id] != -1 ) {
    			int team_patch_i = segmentCountList.get(best_robot_seg_id[id]).team_patch_id;
    			
    			robotHome[id].valid = true;
    			robotHome[id].id = team_patch_i;
    			robotHome[id].pixelPos = teamPatchList.get(team_patch_i).center;
    			robotHome[id].realPos = teamPatchList.get(team_patch_i).realCenter;
    			robotHome[id].revisionPos = teamPatchList.get(team_patch_i).revisionCenter;
    			
    			robotHome[id].direction = segmentCountList.get(best_robot_seg_id[id]).orientation_rad;
    			robotHome[id].pixelDirection = segmentCountList.get(best_robot_seg_id[id]).orientation_screen;

    			robotHome[id].direction += BoardProperties.getRotateAngle();
    			robotHome[id].pixelDirection = BoardProperties.getRotateAngle() - robotHome[id].pixelDirection;
				robotHome[id].realPos.x *= 100;
				robotHome[id].realPos.y = 180-(robotHome[id].realPos.y*100);


    		}
    		else
    		{
    			robotHome[id].valid = false;
    			robotHome[id].id = -1;
    			
    			robotHome[id].pixelPos = new Point(0,0);
    			robotHome[id].realPos = new Point(-10,0+id*10);
    			robotHome[id].revisionPos = new Point(0,0);
    			
    			robotHome[id].direction = 0;
    			robotHome[id].pixelDirection = 0;
    			
    		}

			if (robotHome[id].valid) {
				bFound[id][testCount] = true;
			} else {
				bFound[id][testCount] = false;
			}

			int tempCountLoss = 0;
			for (int t = 0; t<100; t++) {
				if (bFound[id][t]) {
					tempCountLoss++;
				}
			}
            //Set team robot positions
			robots.setPosition(id, robotHome[id].realPos);

		}
		testCount += 1;
		if (testCount >= 100) {
			testCount = 0;
		}

    }

	public void Run_FindBall(Mat image) {
		if (ballPatchList.size() == 0 ) {
			return;
		}

		int numPixels = 0, maxIndex = -1;

		for (int l=0; l<ballPatchList.size(); l++ ) {
			double sumX = 0, sumY = 0;

			for (int p=0; p<ballPatchList.get(l).pixels.size(); p++) {
				sumX += ballPatchList.get(l).pixels.get(p).x;
				sumY += ballPatchList.get(l).pixels.get(p).y;
			}

			ballPatchList.get(l).center.x = sumX/ballPatchList.get(l).pixels.size();
			ballPatchList.get(l).center.y = sumY/ballPatchList.get(l).pixels.size();

			if (numPixels < ballPatchList.get(l).pixels.size()) {
				numPixels = ballPatchList.get(l).pixels.size();
				maxIndex = l;
			}
		}

		double sumX = 0,  sumY = 0;
		if( maxIndex >= 0 )	{
			for (int p=0; p<numPixels; p++) {
				sumX += ballPatchList.get(maxIndex).pixels.get(p).x;
				sumY += ballPatchList.get(maxIndex).pixels.get(p).y;
			}
		}

		Point imageBallPosition = new Point(0,0);
		if( numPixels > 0 ) {
			imageBallPosition.x = sumX / numPixels;
			imageBallPosition.y = sumY / numPixels;
		}
		else {
			imageBallPosition.x = 0;
			imageBallPosition.y = 0;
		}

        //Set ball position
		ball = BoardProperties.imagePosToActualPos(imageBallPosition);
	}
	
	public void Run_FindOpponent() {
		if (enemyPatchList.size() == 0 ) {
			return;
		}
		int count = 1;

		for (int l=0; l<enemyPatchList.size(); l++ ) {
			double sumX = 0, sumY = 0;

			for (int p=0; p<enemyPatchList.get(l).pixels.size(); p++) {
				sumX += enemyPatchList.get(l).pixels.get(p).x;
				sumY += enemyPatchList.get(l).pixels.get(p).y;
			}

			enemyPatchList.get(l).center.x = sumX/enemyPatchList.get(l).pixels.size();
			enemyPatchList.get(l).center.y = sumY/enemyPatchList.get(l).pixels.size();

            //Set opponent positions
			opponents.setPosition(l, new Point(enemyPatchList.get(l).center.x, enemyPatchList.get(l).center.y));

			//count++;
			if (count > 5) count = 5;
		}
	}

}
