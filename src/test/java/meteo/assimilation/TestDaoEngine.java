package meteo.assimilation;

import org.junit.Test;

import meteo.util.Env;

public class TestDaoEngine 
{
	@Test
	public void testDaoEngine()
	{
		
		System.setProperty(Env.ETC_PATH_PROPERTY, "src/test/resources");
		
		DaoEngine engine = DaoEngine.go();
		
		Env.sleep(15000);
		
		engine.stop();
	}
}
