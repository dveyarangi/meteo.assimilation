package meteo.assimilation;

import java.io.PrintStream;
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
 * Main class for Data Assimilation Operations engine
 */
@Slf4j
public class DaoEngine 
{

	
	/**
	 * Self-sufficient main to run without the Spring Boot context
	 * @param args
	 */
	public static void main( String [] args )
	{
		/////////////////////////////////////////////////////
		// show logo and version
		printBanner("Application", DAO_FULL_VERSION, System.out);
		
		/////////////////////////////////////////////////////
		// start the engine
		DaoEngine engine = new DaoEngine();
		
		engine.start();
	}
	
	@Getter DaoCfg config;
	
	@Getter Injector injector;
	
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
		new Thread(engineHeart, "tao-engine").start();
		
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
		// TODO: throttle monitoring errors on constant monitor failures
		log.info("Server has started.");
		
		this.monitors.forEach(FolderMonitor::init);
		
		while( isAlive )
		{
			// update all registered monitors:
			this.monitors.forEach(FolderMonitor::update);

			isAlive |= Env.sleep(INPUT_MONITORING_INTERVAL);
		}
		
		this.monitors.forEach(FolderMonitor::close);		
		
		log.info("Server has stopped.");
	}
	
	public void stop() { this.isAlive = false; }

	/**
	 * Create format assimilators basing on provided configuration
	 * @param config
	 * @return
	 */
	private List<FolderMonitor> createMonitors( Injector injector ) 
	{
		
		List <FolderMonitor> monitors = new ArrayList <> ();
		
		AssListener listener = null;
		try {
			listener = injector.getInstance(AssListener.class);
		}
		catch(ConfigurationException e) { log.debug("No assimilation listener registered."); }
		
		
		DaoCfg config = injector.getInstance( DaoCfg.class );
		
		for(MonitorCfg pathCfg : config.getAssPaths())
		{
			// instantiate and initialize configured assimilator:
			
			FileAssimilator assimilator;
			try {
				@SuppressWarnings("unchecked")
				Class<FileAssimilator> assClass = (Class<FileAssimilator>) Class.forName( pathCfg.getAssimilatorClass() );
				assimilator = injector.getInstance(assClass);
			} 
			catch (ClassNotFoundException x) 
			{
				log.error("Failed to instantiate assimilator type " +  pathCfg.getAssimilatorClass() , x);
				throw new RuntimeException(x);
			}
			
			// create input folder monitoring helper:
			FolderMonitor monitor = new FolderMonitor( pathCfg, assimilator, listener );

			monitors.add( monitor );
		}	
		
		return monitors;
	}
	
	
	/**
	 * Create Guice injector defining the application dependencies graph
	 * @return
	 */
	private Injector createApplication(final DaoCfg config, final AbstractModule ... modules ) 
	{
		List <AbstractModule> allModules = new ArrayList <> ();
		
		allModules.add(config);
		
		allModules.addAll(Arrays.asList(modules));
		
		Injector injector = Guice.createInjector( allModules );
		
		
		return injector;
	}


	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String VERSION_MAJOR = "1";
	public static final String VERSION_MINOR = "00";
	public static final String BUILD_NUMBER = Env.loadBuildNumber("tao.build.number");
	public static final String DAO_FULL_VERSION = Env.toVersionStr(VERSION_MAJOR, VERSION_MINOR, BUILD_NUMBER); 
	
	public static void printBanner(String appName, String appVersion, PrintStream out) 
	{
		out.println("                                                                   ");
		out.println("     .dP^^88b_                                                     ");
		out.println("    d^   d88^8b       DAO  Engine       " + appName);
		out.println("   {8    Y88a8B}      -----------       -----------");
		out.println("    Y. a  )888P       " + DAO_FULL_VERSION + "       " + appVersion);
		out.println("     \"b..a88P\"                                                     ");
		out.println("                                                                   ");
	}
}
