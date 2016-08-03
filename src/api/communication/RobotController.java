package api.communication;

public class RobotController {
	
	private SerialPortCommunicator communicator;
	
	public RobotController(SerialPortCommunicator c) {
		communicator = c;
	}
	
	public void sendVelocity(double[] linearVelocity, double[] angularVelocity) {

		int[] sdata = new int[48];
		
		sdata[0] = 255;
		sdata[1] = 255;
		sdata[2] = 0x01;
		
		
		int checksum = (sdata[0] +sdata[1] + sdata[2]) & 0xff;
		
		for (int i=0; i < 11; i++) {

			double tempLin = linearVelocity[i];
			double tempAng = angularVelocity[i];
			
			if (tempLin < -4.0) {
				tempLin = -4.0;
			} else if (tempLin > 4.0) {
				tempLin =  4.0;
			}

			// Clip the angle. Max 128, min -128.
			if (tempAng < -128.0) {
				tempAng = -128.0;
			} else if (tempAng > 128.0) {
				tempAng =  128.0;
			}
			
			//http://www.javamex.com/java_equivalents/unsigned.shtml
			sdata[4*i+0+3] = (((int)(tempLin*2048.)) & 0xff);
			sdata[4*i+1+3] = (((int)(tempLin*2048.)>>>8) & 0xff);
			sdata[4*i+2+3] = (((int)(tempAng*64.)) & 0xff);
			sdata[4*i+3+3] = (((int)(tempAng*64.)>>>8) & 0xff);
			
			checksum += sdata[4*i+3];
			checksum += sdata[4*i+1+3];
			checksum += sdata[4*i+2+3];
			checksum += sdata[4*i+3+3];
		}
		
		sdata[47] = -checksum & 0xff;
		
		communicator.writeData(sdata);
	}

}
