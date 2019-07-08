package meteo.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class JsonInterfaceAdapter <T> implements JsonDeserializer <T>
{
	public static final String CLASSNAME = "className";

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonElement elem, Type type, JsonDeserializationContext ctx) throws JsonParseException 
	{
		JsonObject jsonObject = elem.getAsJsonObject();
		String className = jsonObject.get(CLASSNAME).getAsString();
		Class<T> clazz;
		try {
			clazz = (Class <T>)Class.forName(className);
		} catch (ClassNotFoundException e) { throw new JsonParseException(e); }
		
		return ctx.deserialize(elem, clazz);
	}

}
