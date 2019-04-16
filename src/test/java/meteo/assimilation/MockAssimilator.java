package meteo.assimilation;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockAssimilator implements FileAssimilator 
{

	@Override
	public String getType() { return "mock-assimilator";
	}

	@Override
	public void init(FileAssimilatorCfg cfg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canAssimilate(File file) throws IOException {
		return true;
	}

	@Override
	public AssOutcome assimilate(File file) throws IOException {
		log.info("Assimilated file '" + file.getName() + "'");
		return AssOutcome.SUCCESS;
	}

}
