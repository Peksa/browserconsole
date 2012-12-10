package controllers;

import java.util.HashMap;
import java.util.TreeSet;

import models.Command;
import models.Result;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

import play.libs.F.EventStream;
import play.mvc.Controller;
import play.mvc.Util;
import util.Listener;

public class Application extends Controller
{
	private static HazelcastInstance hazel = Hazelcast.newHazelcastInstance(null);
	
    public static void index()
    {
        render();
    }
    
    public static void get(String token, String lastId)
    {
    	Result result = new Result();
    	IMap<Long, Command> map = hazel.getMap(token);
		if (lastId != null)
		{
			EntryObject e = new PredicateBuilder().getEntryObject();
			Predicate predicate = e.get("id").greaterThan(lastId);
			
			TreeSet<Command> messages = new TreeSet<>(map.values(predicate));
			if (messages.size() > 0)
			{
				result.commands.addAll(messages);
				result.lastId = messages.last().id;
				renderJSON(result);
			}
		}
		
		Listener<Command> listener = new Listener<>();
		map.addEntryListener(listener, true);
		
		Command message = await(listener.stream.nextEvent());
		map.removeEntryListener(listener);
		
		result.commands.add(message);
		result.lastId = message.id;
		
    	renderJSON(result);
    }
    
    public static void post(String token, String msg)
    {
    	IdGenerator idg = hazel.getIdGenerator("id");
		Long id = idg.newId();
		
	    IMap<Long, Command> map = hazel.getMap(token);
	    map.put(id, new Command(id, msg));
	    
    	renderJSON("ok");
    }
}