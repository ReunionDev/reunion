package org.reunionemu.jreunion.game.items.etc;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemPosition;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.QuickSlotPosition;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;

public class MissionReceiver extends Etc implements Usable{
	
	public MissionReceiver(int id) {
		super(id);
		
	}
	
	@Override
	public Item<?> create() {
		Item<?> item = super.create();
		item.setExtraStats(10);
		return item;
	}
	
	@Override
	public boolean use(Item<?> item, LivingObject user, int quickSlotPosition, int unknown) {
		
		if(user instanceof Player){
			Player player = (Player)user;
			
			player.getClient().sendPacket(Type.SAY, "Quests are temporarily disabled.");
			
			/*
			
			//check if player have the correct level to use this item.
			if(player.getLevel() >= 100){
				player.getClient().sendPacket(Type.SAY, "Your level is to high to use this item.\n" +
						"Please use the Advanced Mission Receiver.");
				return false;
			}
			
			//check if the MR has run out of quests.
			if(item.getExtraStats() <= 0){
				player.getClient().sendPacket(Type.SAY, "Mission Reciever run out of available quests.");
				return false;
			}
			
			Quest quest = player.getClient().getWorld().getQuestManager().getRandomQuest(player);
			
			//check if a quest for the player level have been found.
			if(quest == null){
				player.getClient().sendPacket(Type.SAY, "No quests available for character level.");
				return false;
			} 
			
			item.setExtraStats(item.getExtraStats()-1);
			DatabaseUtils.getDinamicInstance().saveItem(item);
			player.setQuest(quest);
			*/
			
			return true;
		} else {
			LoggerFactory.getLogger(MissionReceiver.class).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
	
	@Override
	public void setExtraStats(Item<?> item) {
		super.setExtraStats(item);
		
		ItemPosition position = item.getPosition();
		if(position instanceof QuickSlotPosition){
			
			QuickSlotPosition quickSlotPosition = (QuickSlotPosition)position;
			Client client = quickSlotPosition.getQuickSlotBar().getPlayer().getClient();
			client.sendPacket(Type.QT, "quick " + quickSlotPosition.getSlot() + " " + item.getExtraStats());
		}
		
	}
}