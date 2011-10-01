package com.googlecode.reunion.jreunion.server.parser.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.game.Inventory;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.parser.PacketHandler;

public class InventoryHandler extends PacketHandler {

	@Override
	public void handle(Client client, Matcher matcher) {
		
		Inventory inventory = getInventory(client);
		if(inventory!=null){
			int tab = Integer.parseInt(matcher.group(1));
			int x = Integer.parseInt(matcher.group(2));
			int y = Integer.parseInt(matcher.group(3));			
			inventory.handleInventory(tab, x, y);				
			
		}

	}
	
	public Inventory getInventory(Client client){
		if(client!=null){
			Player player = client.getPlayer();
			if(player!=null) {
				return player.getInventory();
			}
		}
		return null;
	}


	@Override
	public Pattern[] getPatterns() {
		return new Pattern [] {Pattern.compile("^inven (\\d+) (\\d+) (\\d+)$")};		
		
	}

}
