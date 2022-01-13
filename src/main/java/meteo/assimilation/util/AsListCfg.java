package meteo.assimilation.util;

import java.util.List;

import lombok.Getter;
import meteo.assimilation.FileAssimilator;
import meteo.assimilation.FileAssimilatorCfg;

public class AsListCfg extends FileAssimilatorCfg
{
	@Getter private List <FileAssimilatorCfg> configs;

	@Override public Class<? extends FileAssimilator> getAssimilatorClass() { return AsList.class; }
}
