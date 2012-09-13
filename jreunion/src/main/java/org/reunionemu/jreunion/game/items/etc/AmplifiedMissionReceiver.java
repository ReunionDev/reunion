package org.reunionemu.jreunion.game.items.etc;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Quest;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class AmplifiedMissionReceiver extends MissionReceiver{

	
	public AmplifiedMissionReceiver(int id) {
		super(id);
	}
	
	@Override
	public Item<?> create() {
		Item<?> item = super.create();
		item.setExtraStats(30);
		return item;
	}
	
	@Override
	public boolean use(Item<?> item, LivingObject user, int slot, int unknown) {
		if(user instanceof Player){
			Player player = (Player)user;
			
			//check if player have the correct level to use this item.
			if(player.getLevel() < 100){
				player.getClient().sendPacket(Type.SAY, "Your level is to low to use this item.\n" +
						"Please use the Mission Receiver.");
				return false;
			}
			
			//check if the AMR has run out of quests.
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
			
			return true;
		}	
		return false;
	}

}