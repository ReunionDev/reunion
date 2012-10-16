package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Quest {

	private int id;
	
	private String description;
	
	private int minLevel;
	
	private int maxLevel;
	
	private boolean repeatable;
	
	public Quest(int questId) {
		this.id = questId;
	}
	
	public int getId(){
		return this.id;
	}

	public int getMinLevel(){
		return this.minLevel;
	}
	
	public int getMaxLevel(){
		return this.maxLevel;
	}
	
	public void setMinLevel(int minLevel){
		this.minLevel = minLevel;
	}
	
	public void setMaxLevel(int maxLevel){
		this.maxLevel = maxLevel;
	}
	
	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}
	
	public String getDescrition(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}


	/****** Quest Points Reached Zero ********/
	/*
	 * public void QuestSecondFase(G_Player player){ S_Client client =
	 * S_Server.getInstance().getNetworkModule().getClient(player);
	 * 
	 * if(client==null) return;
	 * 
	 * String packetData = "qt nt\n";
	 * S_Server.getInstance().getNetworkModule().SendPacket
	 * (client.networkId,packetData); }
	 */

	/****** Quest Eff ********/
	public void eff(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		client.sendPacket(Type.QT, "eff " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getEntityId());
	}

	/****** Quest End ********/
	public void end(Player player, int questId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		player.setQuest(null);
		
		client.sendPacket(Type.QT, "end " + questId);
	}

	public void setId(int id) {
		this.id = id;
	}



	/****** Quest Spawn Of Ruin ********/
	public void spawnOfRuin(Player player, int slot) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		String packetData = "usq succ " + slot + "\n";
				client.sendData(packetData);
	}
}