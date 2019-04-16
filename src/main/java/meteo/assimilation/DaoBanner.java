package meteo.assimilation;

import java.io.PrintStream;

import meteo.util.Env;

public class DaoBanner 
{
	
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
