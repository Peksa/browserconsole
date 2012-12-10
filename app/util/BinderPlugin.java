package util; 

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import play.Logger;
import play.PlayPlugin;
import play.data.binding.As;
import play.data.binding.RootParamNode;
import play.mvc.Http;
import play.mvc.Scope;

public class BinderPlugin extends PlayPlugin
{
	public final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	@Override
	public Object bind(RootParamNode parentParamNode, String name, Class<?> clazz, Type type, Annotation[] annotations)
	{
		if (Http.Request.current().contentType.equals("application/json"))
		{
			if (name.equals("json")) {
				return gson.fromJson(Scope.Params.current().get("body"), clazz);
			}
		}
		return null; 
	}
}