package meteo.assimilation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import meteo.util.Env;

/**
 * <p>
 * Main class for Data Assimilation Operations engine.
 * <p>
 * The engine monitors folders and feeds files arriving to those folders into
 * associated {@link FileAssimilator} instances.
 * <p>
 * The assimilators are defined by {@link DaoCfg} (located at folder, 
 * defined by {@link Env#ETC_PATH_PROPERTY} JVM property) 
 * 
 * <p>
 * * use {@link #go()} method to start the engine.
 */
@Slf4j
public class DaoEngine 
{
	
	/**
	 * Start the engine
	 */
	public static DaoEngine go()
	{
		
		/////////////////////////////////////////////////////
		// prepare the engine
		DaoEngine engine = new DaoEngine();
		
		/////////////////////////////////////////////////////
		// start the engine
		engine.start();
		
		return engine;
	}
	
	/**
	 * Engine configuration
	 */
	@Getter DaoCfg config;
	
	/**
	 * Engine dependency graph provider
	 */
	@Getter Injector injector;
	
	/**
	 * Folder monitors
	 */
	@Getter List <FolderMonitor> monitors;

	/**
	 * Application starts here
	 */
	public void start(AbstractModule ... modules)
	{
		
		//////////////////////////////////////////////////////////////
		// load configuration
		config = DaoCfg.loadConfig();
		if( config == null )
		{
			log.error("Failed to load configuration");
			System.exit(1);
		}
		
		//////////////////////////////////////////////////////////////
		// register main modules and create application dependencies graph
		injector = createApplication( config, modules );
		
		//////////////////////////////////////////////////////////////
		// instantiate format parsers and attach their file feeds:
		monitors = createMonitors( injector );

		//////////////////////////////////////////////////////////////
		// tirelessly monitor input folders:
		
		Runnable engineHeart = new Runnable() { @Override public void run()  { runEngine(); } };
		new Thread(engineHeart, "dao-engine").start();
		
		// stop main loop on kill signal
		Runtime.getRuntime().addShutdownHook(new Thread( ()->stop() ));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long INPUT_MONITORING_INTERVAL = 1000;
	
	private volatile boolean isAlive = true;
	
	/**
	 * Monitor input folders
	 * @param monitors
	 */
	private void runEngine()
	{
		
		this.monitors.forEach(FolderMonitor::init);
		
		log.info("=======================");
		log.info("DAO engine has started.");
		log.info("=======================");
		
		// TODO: throttle monitoring errors on constant monitor failures
		
		while( isAlive )
		{
			// update all registered monitors:
			this.monitors.forEach(FolderMonitor::update);

			isAlive |= Env.sleep(INPUT_MONITORING_INTERVAL);
		}
		
		this.monitors.forEach(FolderMonitor::close);		
		
		log.info("=======================");
		log.info("DAO engine has stopped.");
		log.info("=======================");
	}
	
	/**
	 * Create format assimilators basing on provided configuration
	 * @param config
	 * @return
	 */
	private List<FolderMonitor> createMonitors( Injector injector ) 
	{
		
		List <FolderMonitor> monitors = new ArrayList <> ();
		
		AsListener listener = null;
		try {
			listener = injector.getInstance(AsListener.class);
		}
		catch(ConfigurationException e) { log.trace("No assimilation listener registered."); }
		
		DaoCfg config = injector.getInstance( DaoCfg.class );
		
		for(MonitorCfg pathCfg : config.getAssPaths())
		{
			// instantiate and initialize configured assimilator:
			FileAssimilator assimilator = injector.getInstance( pathCfg.getConfig().getAssimilatorClass() );
			
			// create input folder monitoring helper:
			FolderMonitor monitor = new FolderMonitor( pathCfg, assimilator, listener );

			monitors.add( monitor );
		}	
		
		return monitors;
	}
	
	
	/**
	 * Create Guice injector defining the application dependencies graph
	 * @param config - configuration module
	 * @param modules list of optional modules to be provided by application using the engine
	 * @return
	 */
	private Injector createApplication(final DaoCfg config, final AbstractModule ... modules ) 
	{
		List <AbstractModule> allModules = new ArrayList <> ();
		
		// configuration module (this tricky bastard injects itself as singleton)
		allModules.add(config);
		
		allModules.addAll(Arrays.asList(modules));
		
		return Guice.createInjector( allModules );
	}

	/**
	 * Safe stop the engine
	 */
	public void stop() { this.isAlive = false; }

}
