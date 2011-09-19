package com.googlecode.reunion.jreunion.game.items;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Etc extends EtcItem implements Usable{
	public Etc(int id) {
		super(id);
		setExtraStats(10);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	@Override
	public void use(final LivingObject user, int slot) {
		if(user instanceof Player){
			Player player = (Player)user;
			//QuickSlotItem quickSlotItem = player.getQuickSlot().getItem(slot);
			
			if(getExtraStats() <= 0){
				player.getClient().sendPacket(Type.SAY, "Mission Reciever run out of available quests.");
				return;
			}
			
			if(player.getQuest() != null){
				player.getClient().sendPacket(Type.SAY, "Player already has an ongoing quest.");
				return;
			}
			
			setExtraStats(getExtraStats()-1);
			//Quest quest = new Quest(player,quickSlotItem);
			Quest quest = DatabaseUtils.getStaticInstance().getRandomQuest(player);
			player.setQuest(quest);
			player.getQuest().loadQuest(player, quest.getType(),slot);
		}	
	}
}