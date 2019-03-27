package meteo.util;

import java.io.File;
import java.util.Properties;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;
import meteo.assimilation.DaoEngine;

/**
 * Bootstrap environment
 */
@Slf4j
public class Env 
{
	/**
	 * JVM property name for engine configuration path
	 */
	private static final String ETC_PATH_PROPERTY = "etc.dir";

	/**
	 * Default configuration path
	 */
	private static final String ETC_PATH_DEFAULT = "etc";
	
	/**
	 * Actual configuration path
	 */
	private static File etcFile = null;
	
	/**
	 * JVM property name for cache storage path
	 */
	private static final String CACHE_PATH_PROPERTY = "cache.dir";
	/**
	 * Default configuration cache
	 */
	private static final String CACHE_PATH_DEFAULT = "cache";
	
	/**
	 * Actual cache path
	 */
	private static File cacheFile = null;

	static 
	{
		/////////////////////////////////////////////////////
		// determine bootstrap configuration path:
		String etcPath = System.getProperty(ETC_PATH_PROPERTY);
		if( etcPath == null )
		{
			log.debug("Configuration path property \"" + ETC_PATH_PROPERTY + "\" not specified, using default.");
			etcPath = ETC_PATH_DEFAULT;
		}
		etcFile = new File(etcPath);
		if( ! etcFile.exists() )
		{
			log.warn("Configuration path \"" + etcFile.getAbsolutePath() + "\" does not exist.");
			System.exit(1);
		}
		
		String cachePath = System.getProperty(CACHE_PATH_PROPERTY);
		if( cachePath == null )
			cachePath = CACHE_PATH_DEFAULT;
		
		cacheFile = new File( cachePath );
		
		log.info("Using cache path " +  cacheFile.getAbsolutePath());
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	/**
	 * Get configuration resource path
	 * @param subpath
	 * @return
	 */
	public static String etcpath(String subpath)
	{
		return etcFile.getAbsolutePath() + "/" + subpath;
	}
	
	public static String cachePath( String subpath )
	{
		return cacheFile.getAbsoluteFile() + "/" + subpath;
	}
	
	public static String loadBuildNumber(String buildNumberFilename)
	{
		try {
			Properties props = new Properties();
			props.load( DaoEngine.class.getClassLoader().getResourceAsStream(buildNumberFilename) );
			return String.format("%04d", Integer.parseInt(props.getProperty("build.number", "????")));
		} 
		catch (Exception e) 
		{
			return "????"; 
		}
	}
	
	public static String toVersionStr(String major, String minor, String build)
	{
		return String.format("v%s.%s.b%s", major, minor, build);
	}

	/**
	 * @param ms sleep duration
	 * @return should interrupt caller
	 */
	public static boolean sleep(long ms) 
	{
		try {
			Thread.sleep(ms);
			return true;
		} 
		catch (InterruptedException e) { return false; }
	}
}
