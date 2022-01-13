package meteo.assimilation;

import lombok.Getter;
import lombok.Setter;

/**
 * Base interface for file assimilators configuration 
 */
public abstract class FileAssimilatorCfg 
{
	@Getter @Setter String cfgFile;
	public abstract Class<? extends FileAssimilator> getAssimilatorClass();
}
