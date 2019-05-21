package meteo.assimilation;

import java.io.File;

public interface AssListener 
{
	public void fileAssimilated(File file, String type, AssOutcome outcome);
	
	public void assimilationFailed(File file, String type, Throwable error);
}
