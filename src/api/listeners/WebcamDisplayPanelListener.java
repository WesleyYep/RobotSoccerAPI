package api.listeners;

import org.opencv.core.Mat;
import ui.WebcamDisplayPanel.ViewState;

public interface WebcamDisplayPanelListener {
	public void imageUpdated(Mat image);
}
