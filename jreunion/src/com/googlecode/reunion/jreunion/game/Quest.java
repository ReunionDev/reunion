package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Quest {

	private int id;

	private int tp;

	private int pt;

	public Quest(Player player, int slot) {
		getQuest(player, slot);
	}

	/****** Cancel the current player Quest ********/
	public void cancelQuest(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		player.setQuest(null);
		client.sendPacket(Type.SAY, "Quest cancelled.");
		client.sendPacket(Type.QT, "get -1");
	}

	/****** Update Quest Points Obtained ********/
	public void changeQuestPT(Player player, int remainPoints,
			int obtainedPoints) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		client.sendPacket(Type.QT, "pt " + remainPoints + " " + obtainedPoints);

		/****** Quest Points Reached Zero ********/
		if (remainPoints == 0) {
			Item item = ItemFactory.create(1053);

			item.setExtraStats(0);
			item.setGemNumber(0);

			// QuestSecondFase(player);
			
			client.sendPacket(Type.QT, "nt");
			player.getInventory().storeItem(item);
			
		}
	}

	/****** Update Quest Total points ********/
	public void changeQuestTP(Player player, int tp) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		client.sendPacket(Type.QT, "tp " + tp);
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

	public int getID() {
		return id;
	}

	public int getPT() {
		return pt;
	}

	/****** Get quest ********/
	public void getQuest(Player player, int slot) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		//int questId = 669;
		int questId =  86 + (int)Math.random()*10;

		QuickSlotItem qsItem = player.getQuickSlot().getItem(slot);
		
		client.sendPacket(Type.QT, "get " + questId);

		if (questId == 669) {
			double tp = Math.random() * 2000 + 300;

			changeQuestTP(player, (int) tp);
			changeQuestPT(player, (int) tp, 0);
		}

		updateMissionReceiver(player, slot,
				qsItem.getItem().getExtraStats() - 1);
	}

	public int getTP() {
		return tp;
	}

	/****** Quest Eff ********/
	public void questEff(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		client.sendPacket(Type.QT, "eff " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getEntityId());
	}

	/****** Quest End ********/
	public void questEnd(Player player, int questId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		player.setQuest(null);
		
		client.sendPacket(Type.QT, "end " + questId);
	}

	/****** Quest Kill ********/
	public void questKill(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		int pos = 0;
		int ammount = 1;
		
		client.sendPacket(Type.QT, "kill " + pos + " " + ammount);
		// S> qt kill [Pos] [Ammount]
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setPT(int pt) {
		this.pt = pt;
	}

	public void setTP(int tp) {
		this.tp = tp;
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

	/****** Update the Mission Receiver in the Quick Slot ********/
	public void updateMissionReceiver(Player player, int slot,
			int missionsRemaining) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		QuickSlotItem qsItem = player.getQuickSlot().getItem(slot);
		qsItem.getItem().setExtraStats(missionsRemaining);

		client.sendPacket(Type.QT, "quick " + slot + " " + missionsRemaining);
	}
}