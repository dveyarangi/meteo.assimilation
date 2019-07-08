package meteo.assimilation;

import java.io.File;

public interface AsListener 
{
	public void fileAssimilated(File file, String type, AsOutcome outcome);
	
	public void assimilationFailed(File file, String type, Throwable error);
}
