package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Quest {

	private int id;

	private int tp;

	private int pt;

	public G_Quest(G_Player player, int slot) {
		getQuest(player, slot);
	}

	/****** Cancel the current player Quest ********/
	public void cancelQuest(G_Player player) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		player.setQuest(null);
		String packetData = "qt get -1\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
	}

	/****** Update Quest Points Obtained ********/
	public void changeQuestPT(G_Player player, int remainPoints,
			int obtainedPoints) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		String packetData = "qt pt " + remainPoints + " " + obtainedPoints
				+ "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);

		/****** Quest Points Reached Zero ********/
		if (remainPoints == 0) {
			G_Item item = new G_Item(1053);

			item.loadFromReference(1053);
			item.setExtraStats(0);
			item.setGemNumber(0);

			// QuestSecondFase(player);
			packetData = "qt nt\n";
			S_Server.getInstance().getNetworkModule()
					.SendPacket(client.networkId, packetData);
			player.pickupItem(item.getEntityId());
		}
	}

	/****** Update Quest Total points ********/
	public void changeQuestTP(G_Player player, int tp) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		String packetData = "qt tp " + tp + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
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
	public void getQuest(G_Player player, int slot) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		int questId = 669;// (int)Math.random()*669;

		G_QuickSlotItem qsItem = player.getQuickSlot().getItem(slot);
		String packetData = "qt get " + questId + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);

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
	public void questEff(G_Player player) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		String packetData = "qt eff " + player.getPosX() + " "
				+ player.getPosY() + " " + player.getEntityId() + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
	}

	/****** Quest End ********/
	public void questEnd(G_Player player, int questId) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		player.setQuest(null);
		String packetData = "qt end " + questId + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
	}

	/****** Quest Kill ********/
	public void questKill(G_Player player) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		int pos = 0;
		int ammount = 1;

		String packetData = "qt kill " + pos + " " + ammount + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
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
	public void spawnOfRuin(G_Player player, int slot) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		String packetData = "usq succ " + slot + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
	}

	/****** Update the Mission Receiver in the Quick Slot ********/
	public void updateMissionReceiver(G_Player player, int slot,
			int missionsRemaining) {
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_QuickSlotItem qsItem = player.getQuickSlot().getItem(slot);
		qsItem.getItem().setExtraStats(missionsRemaining);

		String packetData = "qt quick " + slot + " " + missionsRemaining + "\n";
		S_Server.getInstance().getNetworkModule()
				.SendPacket(client.networkId, packetData);
	}
}