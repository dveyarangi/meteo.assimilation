package meteo.assimilation;

public class MockAssimilatorCfg extends FileAssimilatorCfg
{

	@Override
	public Class<? extends FileAssimilator> getAssimilatorClass()
	{
		return MockAssimilator.class;
	}

}
