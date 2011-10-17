package com.googlecode.reunion.jreunion.game.quests.objective;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class PointsObjective extends Objective{
	
	private int currentPoints; //obtained points
	
	public PointsObjective(int id, int ammount) {
		super(id, ammount);
	}
	
	public int getCurrentPoints() {
		return currentPoints;
	}
	
	public void setCurrentPoints(int currentPoints) {
		this.currentPoints = currentPoints;
	}
	
	/****** Update Quest Points Obtained ********/
	public void changeCurrentPoints(Player player, int remainPoints,
			int obtainedPoints) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		ItemManager itemManager = client.getWorld().getItemManager();
		client.sendPacket(Type.QT, "pt " + remainPoints + " " + obtainedPoints);

		/****** Quest Points Reached Zero ********/
		if (remainPoints == 0) {
			Item<?> item = itemManager.create(1053);

			item.setExtraStats(0);
			item.setGemNumber(0);

			// QuestSecondFase(player);
			
			client.sendPacket(Type.QT, "nt");
			player.getInventory().storeItem(item);
			
		}
	}
}