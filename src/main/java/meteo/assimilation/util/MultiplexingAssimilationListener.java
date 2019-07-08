package meteo.assimilation.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import meteo.assimilation.AsListener;
import meteo.assimilation.AsOutcome;

public class MultiplexingAssimilationListener implements AsListener 
{
	List <AsListener> listeners = new ArrayList <> ();

	public void addListener(AsListener l) { listeners.add(l); }
	public void removeListener(AsListener l) { listeners.remove(l); }
	@Override
	public void fileAssimilated(File file, String type, AsOutcome outcome) {
		for(int idx = 0; idx < listeners.size(); idx ++)
			listeners.get(idx).fileAssimilated(file, type, outcome);
	}
	@Override
	public void assimilationFailed(File file, String type, Throwable error) {
		for(int idx = 0; idx < listeners.size(); idx ++)
			listeners.get(idx).assimilationFailed(file, type, error);
	}
}
