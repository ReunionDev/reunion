package com.googlecode.reunion.jreunion.game.items.etc;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

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
	public void use(Item<?> item, LivingObject user) {
		if(user instanceof Player){
			Player player = (Player)user;
			
			//check if player have the correct level to use this item.
			if(player.getLevel() < 100){
				player.getClient().sendPacket(Type.SAY, "Your level is to low to use this item.\n" +
						"Please use the Mission Receiver.");
				return;
			}
			
			//check if the AMR has run out of quests.
			if(item.getExtraStats() <= 0){
				player.getClient().sendPacket(Type.SAY, "Mission Reciever run out of available quests.");
				return;
			}
			
			Quest quest = player.getClient().getWorld().getQuestManager().getRandomQuest(player);
			
			//check if a quest for the player level have been found.
			if(quest == null){
				player.getClient().sendPacket(Type.SAY, "No quests available for character level.");
				return;
			} 
			
			item.setExtraStats(item.getExtraStats()-1);
			DatabaseUtils.getDinamicInstance().saveItem(item);
			player.setQuest(quest);
		}	
	}

}