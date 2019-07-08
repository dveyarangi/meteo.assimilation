package meteo.assimilation;

public class MockAssimilatorCfg implements FileAssimilatorCfg
{

	@Override
	public Class<? extends FileAssimilator> getAssimilatorClass()
	{
		return MockAssimilator.class;
	}

}
