package com.googlecode.reunion.jreunion.game.quests;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class PointsQuest extends Quest{
	
	private int totalPoints;
	
	public PointsQuest(int questId) {
		super(questId);
	}
	
	public int getTotalPoints() {
		return totalPoints;
	}
	
	public void setTotalPoints(int totalPoints){
		this.totalPoints = totalPoints;
	}
	
	/****** Update Quest Total points ********/
	public void changeTotalPoints(Player player, int tp) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		client.sendPacket(Type.QT, "tp " + tp);
	}
}