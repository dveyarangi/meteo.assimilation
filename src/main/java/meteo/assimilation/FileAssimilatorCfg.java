package meteo.assimilation;

/**
 * Base interface for file assimilators configuration 
 */
public interface FileAssimilatorCfg 
{
	Class<? extends FileAssimilator> getAssimilatorClass();
}
