package meteo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Bootstrap environment
 */
@Slf4j
public class Env 
{
	/**
	 * JVM property name for engine configuration path
	 */
	public static final String ETC_PATH_PROPERTY = "etc.dir";

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
	public static final String CACHE_PATH_PROPERTY = "cache.dir";
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
	
	public static BuildProps loadBuildProps(String buildNumberFilename, String versionMajor, String versionMinor)
	{
		return new BuildProps(buildNumberFilename, versionMajor, versionMinor);
	}

	public static String toVersionStr(String major, String minor, String build)
	{
		return String.format("%s.%s.b%s", major, minor, build);
	}

	/**
	 * @param ms sleep duration
	 * @return isAlive/should interrupt caller
	 */
	public static boolean sleep(long ms) 
	{
		try {
			Thread.sleep(ms);
			return true;
		} 
		catch (InterruptedException e) { return false; }
	}
	
	public static class BuildProps
	{
		@Getter String timestamp;
		
		@Getter String number;
		
		@Getter String version;
		
		public BuildProps(String buildNumberFilename, String versionMajor, String versionMinor)
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(Env.class.getClassLoader().getResourceAsStream(buildNumberFilename))))
			{
				reader.readLine();
				String rawTimestampLine = reader.readLine().substring(1);
				
				timestamp = PRESENTATION_FMT.format(BUILDFILE_FMT.parse(rawTimestampLine));
				
				String rawNumberLine = reader.readLine();
				number = String.format("%04d", Integer.parseInt(rawNumberLine.split("=")[1]));
				
				version = Env.toVersionStr(versionMajor, versionMinor, number);
			
			} catch (IOException e) {
				log.warn("Failed to read build properties");
			} catch (ParseException e) {
				log.warn("Failed to read build properties");
			}
		}
		//Tue May 14 08:46:16 UTC 2019
		private static SimpleDateFormat BUILDFILE_FMT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		private static SimpleDateFormat PRESENTATION_FMT = new SimpleDateFormat("dd-MM-yyyy");
	}
}
