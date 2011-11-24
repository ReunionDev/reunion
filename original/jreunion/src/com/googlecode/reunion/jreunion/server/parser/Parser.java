package com.googlecode.reunion.jreunion.server.parser;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;


public class Parser {
	
	private HashMap<Pattern, PacketHandler> handlers = new HashMap<Pattern, PacketHandler>();

	public Parser() {
		
	}
	
	public HashMap<Pattern, PacketHandler> getHandlers(){
		
		return handlers;
		
	}
	
	public void register(PacketHandler handler){
		
		synchronized(handlers){
			unregister(handler);
			for(Pattern pattern: handler.getPatterns()){
				handlers.put(pattern, handler);				
			}
		}
	}
	public void unregister(PacketHandler handler){
		
		synchronized(handlers){
			
			while (handlers.values().remove(handler));
		}
	}
	
	public void parse(Client client, String line) {
		synchronized(handlers){
			for(Entry<Pattern, PacketHandler> entry : handlers.entrySet()){
				Pattern pattern = entry.getKey();
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()){
					PacketHandler handler = entry.getValue();
					handler.handle(client, matcher);				
				}
			}
		}
	}
}
