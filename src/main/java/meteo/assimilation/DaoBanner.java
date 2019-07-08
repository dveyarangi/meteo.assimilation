package meteo.assimilation;

import java.io.PrintStream;

import meteo.util.Env;
import meteo.util.Env.BuildProps;

public class DaoBanner 
{
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final BuildProps BUILD_PROPS = Env.loadBuildProps("dao.build.number", "1", "00");
	
	public static void printBanner(String appName, BuildProps appProps, PrintStream out) 
	{
		String appTimestamp = appProps == null ? "????-??-??" : appProps.getTimestamp();
		String appVersion   = appProps == null ? "????" : appProps.getVersion();
		out.println("                                                                                   ");
		out.println("                                                                                   ");
		out.println("                     .dP^^88b_                                                     ");
		out.println("   DAO Engine       d^   d88^8b       " + appName);
		out.println("   "+BUILD_PROPS.getTimestamp()+"      {8    Y88a8B}      "+ appTimestamp);
		out.println("   "+BUILD_PROPS.getVersion()+"       Y. a  )888P       " + appVersion);
		out.println("                     \"b..a88P\"                                                     ");
		out.println("                                                                                    ");
	}
}
