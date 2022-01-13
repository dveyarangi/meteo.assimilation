package meteo.assimilation.util;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import meteo.assimilation.FileAssimilatorCfg;
import meteo.util.Env;
import meteo.util.JsonClassAdapter;
import meteo.util.JsonInterfaceAdapter;


public class JsonAsCfgAdapter implements JsonDeserializer <FileAssimilatorCfg>
{
	Gson gson = new GsonBuilder()
			.registerTypeAdapter(Class.class, new JsonClassAdapter())
			.registerTypeAdapter(FileAssimilatorCfg.class, new JsonInterfaceAdapter<>())
			.create();

	@Override
	public FileAssimilatorCfg deserialize(JsonElement elem, Type type, JsonDeserializationContext ctx) throws JsonParseException 
	{
		JsonObject jsonObject = elem.getAsJsonObject();
		JsonElement filenameElement = jsonObject.get("file");
		if( filenameElement == null )
			return new JsonInterfaceAdapter<FileAssimilatorCfg>().deserialize(elem, type, ctx);
		else
		{
			String fullPath = Env.etcpath(filenameElement.getAsString());
			try (FileReader reader = new FileReader(fullPath))
			{
				FileAssimilatorCfg cfg = gson.fromJson(reader, FileAssimilatorCfg.class);
				cfg.setCfgFile(fullPath);
				return cfg;
			} 
			catch( IOException e )
			{
				throw new JsonParseException("Failed to read file " + fullPath + ": " + e.getMessage(), e);
			}
		}
	}

}
