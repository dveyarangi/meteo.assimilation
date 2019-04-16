package meteo.assimilation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import meteo.util.Env;
import meteo.util.JsonClassAdapter;
import meteo.util.JsonInterfaceAdapter;

/**
 * Assimilator engine configuration object.
 * Some black magic is used here to inject the configuration objects into the 
 * dependent modules
 */
@Singleton
@Slf4j
public class DaoCfg extends AbstractModule
{


	@Override
	protected void configure() 
	{
		// this module also serves as POJO dependency for classes that need this config
		bind(DaoCfg.class).toInstance(this);
	}
	
	/**
	 * List of configured assimilation routes
	 */
	@Getter private List <MonitorCfg> assPaths = new ArrayList <> ();
	
	private OutputCfg output = new DefaultOutputCfg();
	
	@Provides // expose output config to dependency injection
	public OutputCfg getOutput() { return output; }


	/**
	 * Load assimilators configuration
	 * @param configFile
	 * @return
	 */
	public static DaoCfg loadConfig() 
	{
		log.info("Using configuration path " + Env.etcpath(""));
		
		String cfgFilename = System.getProperty("dao.cfg");
		if( cfgFilename == null )
			cfgFilename = "dao.conf";
		
		String configFile = Env.etcpath( cfgFilename );
		
		Gson gson = new GsonBuilder()
			// needed to load FormatAssimilatorCfg implementations from json:
			.registerTypeAdapter(FileAssimilatorCfg.class, new JsonInterfaceAdapter<>())
			.registerTypeAdapter(OutputCfg.class, new JsonInterfaceAdapter<>())
			// loads Classes
			.registerTypeAdapter(Class.class, new JsonClassAdapter())
			.create();
		
		
		DaoCfg config = null;
		try (FileReader reader = new FileReader(configFile))
		{
			config = gson.fromJson(reader, DaoCfg.class);
		} 
		catch (FileNotFoundException e) { log.error("Cannot find file "+ configFile); }
		catch (IOException e) { log.error("Failed to read file " + configFile, e); }
		
		if( config == null )
			return null;
		
		String validationWarning = config.validate();
		if( validationWarning != null )
		{
			log.error("Invalid configuration: " + validationWarning);
			return null;
		}
		
		return config;
	}
	
	
	/**
	 * Configuration validity check
	 */
	private String validate()
	{
		if( assPaths == null || assPaths.isEmpty())
			return "No assimilators specified";

		File outDir = new File(output.getOutputDir());
		if( !outDir.exists())
			outDir.mkdirs();

		return null;
	}
}
