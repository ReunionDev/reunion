package org.reunionemu.jreunion.game.quests.objective;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

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
			player.getInventory().storeItem(item, -1);
			
		}
	}
}