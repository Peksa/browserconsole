package controllers;

import java.util.HashMap;
import java.util.TreeSet;

import models.Command;
import models.Message;
import models.Response;
import models.Result;
import models.Status;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
import ua_parser.Client;
import ua_parser.Parser;
import util.Listener;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

public class Application extends Controller
{
	private static HazelcastInstance hazel = Hazelcast.newHazelcastInstance(null);
	private static Parser uaParser;
	
	static
	{
		uaParser = new Parser(WS.url("https://raw.github.com/tobie/ua-parser/master/regexes.yaml").get().getStream());
	}
	
    public static void index()
    {
        render();
    }
    
    public static void get(String token, String lastId)
    {
    	Result result = new Result();
    	IMap<Long, Message> map = hazel.getMap(token);
		if (lastId != null)
		{
			EntryObject e = new PredicateBuilder().getEntryObject();
			Predicate predicate = e.get("id").greaterThan(lastId);
			
			TreeSet<Message> messages = new TreeSet<>(map.values(predicate));
			if (messages.size() > 0)
			{
				result.messages.addAll(messages);
				result.lastId = messages.last().getId();
				renderJSON(result);
			}
		}
		
		Listener<Message> listener = new Listener<>();
		map.addEntryListener(listener, true);
		
		Message message = await(listener.stream.nextEvent());
		map.removeEntryListener(listener);
		
		result.messages.add(message);
		result.lastId = message.getId();
		
    	renderJSON(result);
    }
    
    public static void postCommand(String token, @Valid Command json)
    {
    	IdGenerator idg = hazel.getIdGenerator("id");
		Long id = idg.newId();
		
	    IMap<Long, Message> map = hazel.getMap(token);
	    map.put(id, new Command(id, json.command));
	    
    	renderJSON(new Status("ok"));
    }
    
    public static void postResponse(String token, @Valid Response json)
    {
    	IdGenerator idg = hazel.getIdGenerator("id");
		Long id = idg.newId();
		
	    IMap<Long, Message> map = hazel.getMap(token);
	    Client client = uaParser.parse(request.headers.get("user-agent").value());
	    String browser = client.userAgent.family + " " + client.userAgent.major;
	    
	    map.put(id, new Response(id, json.forId, json.response, browser));
	    
    	renderJSON("ok");
    }
    
    @Before
    static void performValidation()
    {
    	if (validation.hasErrors())
    	{
			HashMap<String, String> errors = new HashMap<>();
    		for (Error error : validation.errors())
    		{
    			String field = error.getKey();
    			int index = field.indexOf(".");
    			if (index == -1)
    				continue;
    			field = field.substring(index+1);
    			
    			errors.put(field, error.message()); 
    		}
    		response.status = 400;
    		renderJSON(new Status("bad request", errors));
    	}
    }
}