package meteo.assimilation;

import java.io.File;
import java.io.IOException;

/**
 * Generic file consumer for use by {@link FolderMonitor}
 */
public interface FileAssimilator
{
	/**
	 * File assimilator id
	 */
	public String getType();
	
	/**
	 * Place to configure the assimilator and load heavy resources
	 * @param cfg
	 */
	public void init(FileAssimilatorCfg cfg);
	
	/**
	 * Test whether file can be processed by this assimilator
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public boolean canAssimilate(File file) throws IOException;
	
	/**
	 * Process the file
	 * @param file
	 * @return hasErrors
	 * @throws IOException
	 */
	public AsOutcome assimilate(File file)  throws IOException;
}
