package meteo.assimilation;

import java.io.PrintStream;

import meteo.util.Env;

public class DaoBanner 
{
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String VERSION_MAJOR = "1";
	public static final String VERSION_MINOR = "00";
	public static final String BUILD_NUMBER = Env.loadBuildNumber("dao.build.number");
	public static final String DAO_FULL_VERSION = Env.toVersionStr(VERSION_MAJOR, VERSION_MINOR, BUILD_NUMBER); 
	
	public static void printBanner(String appName, String appVersion, PrintStream out) 
	{
		out.println("                                                                                   ");
		out.println("                                                                                   ");
		out.println("                     .dP^^88b_                                                     ");
		out.println("   DAO Engine       d^   d88^8b       " + appName);
		out.println("   ----------      {8    Y88a8B}      ----------");
		out.println("   "+DAO_FULL_VERSION+"       Y. a  )888P       " + appVersion);
		out.println("                     \"b..a88P\"                                                     ");
		out.println("                                                                                    ");
	}
}
