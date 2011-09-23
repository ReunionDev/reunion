package com.googlecode.reunion.jreunion.game.items.etc;

public class AmplifiedMissionReceiver extends MissionReceiver{

	
	public AmplifiedMissionReceiver(int id) {
		super(id);
	}
	
	/*
	@Override
	public void use(LivingObject user, int slot) {
		if(user instanceof Player){
			Player player = (Player)user;
			
			if(player.getLevel() < 100){
				player.getClient().sendPacket(Type.SAY, "Your level is to low to use this item.\n" +
						"Please use the Mission Receiver.");
				return;
			}
			
			if(getExtraStats() <= 0){
				player.getClient().sendPacket(Type.SAY, "Advanced Mission Reciever run out of available quests.");
				return;
			}
			
			if(player.getQuest() != null){
				player.getClient().sendPacket(Type.SAY, "Player already has an ongoing quest.");
				return;
			}
			
			setExtraStats(getExtraStats()-1);
			DatabaseUtils.getDinamicInstance().saveItem(this);
			Quest quest = DatabaseUtils.getStaticInstance().getRandomQuest(player);
			player.setQuest(quest);
			player.getQuest().load(player, slot);
		}	
	}
	*/

}