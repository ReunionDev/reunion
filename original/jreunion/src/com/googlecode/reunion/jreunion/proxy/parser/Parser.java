package com.googlecode.reunion.jreunion.proxy.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.packets.Packet;
import com.googlecode.reunion.jreunion.server.parser.Parseable;


public class Parser {
	
	private HashMap<Pattern, Parseable<?>> handlers = new HashMap<Pattern, Parseable<?>>();

	public Parser() {
		
	}
	
	public HashMap<Pattern, Parseable<?>> getHandlers(){
		return handlers;
	}
	
	public void register(Parseable<?> handler){
		synchronized(handlers){
			unregister(handler);
			for(Pattern pattern: handler.getPatterns()){
				handlers.put(pattern, handler);				
			}
		}
	}
	
	public void unregister(Parseable<?> handler){
		synchronized(handlers){
			while (handlers.values().remove(handler));
		}
	}
	
	public List<Packet> parse(String line) {
		List<Packet> results = new LinkedList<Packet>(); 
		synchronized(handlers){
			for(Entry<Pattern, Parseable<?>> entry : handlers.entrySet()){
				Pattern pattern = entry.getKey();
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()){
					Parseable<?> handler = entry.getValue();					
					Packet packet = handler.parse(matcher);
					if(packet!=null){
						results.add(packet);
					}
				}
			}
		}
		return results;
	}
}
