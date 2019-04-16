package meteo.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonClassAdapter implements JsonDeserializer <Class<?>>
{

	@Override
	public Class<?> deserialize(JsonElement elem, Type type, JsonDeserializationContext ctx) throws JsonParseException 
	{
		String className = elem.getAsString();
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) { throw new JsonParseException(e); }
		
		return clazz;
	}

}
