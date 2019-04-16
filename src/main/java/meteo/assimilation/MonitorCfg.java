package meteo.assimilation;

import java.io.File;

import lombok.Getter;
/**
 * Defines assimilation route configuration for a datatype
 */
public class MonitorCfg 
{
	String inputDir;
	String doneDir;
	String errorDir;
	String tempDir;
	@Getter Class<? extends FileAssimilator> assimilatorClass;
	@Getter FileAssimilatorCfg config;
	
	
	public File getInputDir() { return getDir(inputDir); }
	public File getDoneDir()  { return doneDir == null ? null : getDir(doneDir); }
	public File getErrorDir() { return errorDir == null ? null : getDir(errorDir); }
	public File getTempDir()  { return tempDir == null ? null : getDir(tempDir); }
	
	
	private File getDir(String folderName) 
	{
		if( folderName == null )
			return null;
		
		File folder = new File(folderName);
		folder.mkdirs();
		if(!folder.exists())
			throw new IllegalStateException("Failed to create folder " + folder);
		return folder;
	}
	
}
