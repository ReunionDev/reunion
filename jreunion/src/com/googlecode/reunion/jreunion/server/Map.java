package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.game.Position;

public abstract class Map extends EventBroadcaster implements EventListener {
	
	private int id;
	private String name;
	private InetSocketAddress address;


	public Map(int id){
		
		
		this.id = id;
	}	
	
	public boolean isLocal() {
		return this instanceof LocalMap;
	}	
	
	public int getId() {
		return id;
	}
		
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void load() {
		ParsedItem config = Reference.getInstance().getMapConfigReference().getItemById(id);
		ParsedItem map = Reference.getInstance().getMapReference().getItemById(id);
		String ip= config.getMemberValue("Ip");
		int port = Integer.parseInt(config.getMemberValue("Port"));		
		setName(map.getName());
		setAddress(new InetSocketAddress(ip, port));		
	}
	
	/**
	 * @param name the name to set
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "{id: " + getId() + ", name: '" + getName() + "'}";
	}
	
	
}
