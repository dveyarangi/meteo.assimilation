package meteo.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import meteo.assimilation.AssListener;
import meteo.assimilation.AssOutcome;

public class MultiplexingAssimilationListener implements AssListener 
{
	List <AssListener> listeners = new ArrayList <> ();

	public void addListener(AssListener l) { listeners.add(l); }
	public void removeListener(AssListener l) { listeners.remove(l); }
	@Override
	public void fileAssimilated(File file, String type, AssOutcome outcome) {
		for(int idx = 0; idx < listeners.size(); idx ++)
			listeners.get(idx).fileAssimilated(file, type, outcome);
	}
	@Override
	public void assimilationFailed(File file, String type, Throwable error) {
		for(int idx = 0; idx < listeners.size(); idx ++)
			listeners.get(idx).assimilationFailed(file, type, error);
	}
}
