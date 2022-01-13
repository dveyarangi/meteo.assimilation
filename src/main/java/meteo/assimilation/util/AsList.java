package meteo.assimilation.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;

import lombok.Getter;
import meteo.assimilation.AsOutcome;
import meteo.assimilation.FileAssimilator;
import meteo.assimilation.FileAssimilatorCfg;

public class AsList implements FileAssimilator
{
	
	private Injector injector;
	
	
	private AsListCfg cfg;
	@Getter private FileAssimilator sampleAssimilator;

	
	private List <FileAssimilator> asList;

	
	@Inject
	public AsList(Injector injector)
	{
		this.injector = injector;
		
		this.asList = new ArrayList <> ();
	}
	
	@Override
	public void init( FileAssimilatorCfg cfg )
	{
		this.cfg = (AsListCfg) cfg;
		if( this.cfg.getConfigs() == null || this.cfg.getConfigs().isEmpty())
			throw new IllegalArgumentException("No child assimilators defined");
		for(FileAssimilatorCfg asCfg : this.cfg.getConfigs())
		{
			if( cfg == null )
				continue; // can happen when there is a redundant comma in cfg json
			
			FileAssimilator child = injector.getInstance(asCfg.getAssimilatorClass());
			
			if(this.sampleAssimilator == null )
				this.sampleAssimilator = child;
			else
				if(!sampleAssimilator.getType().equals(child.getType()))
					throw new IllegalArgumentException("Assimilators have different types");
			
			child.init(asCfg);
			
			asList.add(child);
		}

	}

	@Override
	public boolean canAssimilate( File file ) throws IOException
	{
		return sampleAssimilator.canAssimilate( file );
	}

	@Override
	public AsOutcome assimilate( File file ) throws IOException
	{
		AsOutcome totalOutcome = AsOutcome.SUCCESS;
		for(FileAssimilator as : asList)
		{
			AsOutcome outcome = as.assimilate(file);
			if(outcome == AsOutcome.ERROR)
				totalOutcome = outcome;
		}
		
		return totalOutcome;
	}

	@Override
	public String getType() { return sampleAssimilator.getType(); }

}
