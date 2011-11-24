package org.reunionemu.jreunion.game.quests;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Quest;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.PacketFactory.Type;

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