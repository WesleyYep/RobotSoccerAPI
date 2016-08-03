package api.vision;

import ui.SamplingPanel;

public interface ColourRangeListener {

	public void hRangeChanged(int max, int min, SamplingPanel panel);
	public void sRangeChanged(int max, int min, SamplingPanel panel);
	public void vRangeChanged(int max, int min, SamplingPanel panel);
}
