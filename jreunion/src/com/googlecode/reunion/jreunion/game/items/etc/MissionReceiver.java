package com.googlecode.reunion.jreunion.game.items.etc;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class MissionReceiver extends Etc implements Usable{
	
	public MissionReceiver(int id) {
		super(id);
		setExtraStats();
	}

	public void setExtraStats(){
		if(this instanceof AmplifiedMissionReceiver)
			setExtraStats(30);
		else
			setExtraStats(10);
	}
	
	@Override
	public void use(LivingObject user, int slot) {
		if(user instanceof Player){
			Player player = (Player)user;
			
			if(this instanceof AmplifiedMissionReceiver){
				if(player.getLevel() < 100){
					player.getClient().sendPacket(Type.SAY, "Your level is to low to use this item.\n" +
							"Please use the Mission Receiver.");
					return;
				}
			} else {
				if(player.getLevel() >= 100){
					player.getClient().sendPacket(Type.SAY, "Your level is to high to use this item.\n" +
							"Please use the Advanced Mission Receiver.");
					return;
				}
			}
			
			if(getExtraStats() <= 0){
				player.getClient().sendPacket(Type.SAY, "Mission Reciever run out of available quests.");
				return;
			}
			
			if(player.getQuest() != null){
				player.getClient().sendPacket(Type.SAY, "Player already has an ongoing quest.");
				return;
			}
			
			Quest quest = player.getClient().getWorld().getQuestManager().getRandomQuest(player);
			
			if(quest == null){
				player.getClient().sendPacket(Type.SAY, "No quests available for character level.");
				return;
			} 
			
			setExtraStats(getExtraStats()-1);
			DatabaseUtils.getDinamicInstance().saveItem(this);
			player.setQuest(quest);
			
			Client client = player.getClient();
			
			client.sendPacket(Type.QT, "quick " + slot + " " + this.getExtraStats());
			
		}	
	}

}