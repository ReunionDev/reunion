package com.googlecode.reunion.jreunion.game.quests;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.InventoryPosition;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.quests.objective.*;
import com.googlecode.reunion.jreunion.game.quests.reward.*;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class QuestState {
	
	Quest quest;
	Map<Objective, Integer> progression = new HashMap <Objective, Integer>();
	
	public QuestState(Quest quest) {
		this.quest = quest;
	}
	
	public void increase(Objective objective){
	
		if(!quest.getObjectives().contains(objective)){
			throw new RuntimeException("Quest "+quest.getId()+" does not have objective "+objective.getId());
		}
		
		if(!progression.containsKey(objective)){
			progression.put(objective, 0);
			return;
		}		
		
		progression.put(objective, progression.get(objective) + 1);		
	}
	
	public Quest getQuest(){
		return this.quest;
	}
	
	public void setProgression(Objective objective, int ammount){
		
		if(objective == null)
			return;
		
		progression.put(objective, ammount);
	}
	
	public int getProgression(Objective objective){
		return progression.get(objective); //if objective not found, it returns null
	}
	
	public int getProgression(int id){
		
		for( Objective objective: progression.keySet()){
			//Logger.getLogger(QuestState.class).debug("OBJECTIVE ID: "+objective.getId());
			if(objective.getId() == id)
				return getProgression(objective);
		}
		
		return 0;
	}
	
	public void handleProgress(Mob mob, Player player){
		for(Objective objective: getQuest().getObjectives()){
			if(objective instanceof MobObjective){
				handleMobProgress(objective, mob, player);
			} else {
				if(objective instanceof PointsObjective){
					handlePointsProgress(mob);
				}
			}
		}
	}
	
	public void handleMobProgress(Objective objective, Mob mob, Player player){
		int mobType = objective.getId();
		
		if(mobType == mob.getType()){
			
			if(!progression.containsKey(objective)) //we need to make sure the HashMap is not empty
				increase(objective);
			
			if(progression.get(objective) < objective.getAmmount()){
				
				Client client = player.getClient();
				if (client == null) return;
				
				increase(objective);

				int slot = quest.getObjectiveSlot(mobType);
				int ammountRemaining = objective.getAmmount() - progression.get(objective);
				
				if(!(quest instanceof ExperienceQuest)){
					client.sendPacket(Type.QT, "kill " + slot + " " + ammountRemaining);
				}
				
				if(quest instanceof ExperienceQuest){
					if(ammountRemaining == 1)
						client.sendPacket(Type.INFO, "Boss is near!");
				}
				
				if(isComplete()){
					endQuest(player, mob);
				}
			}
		}
	}
	
	public void handlePointsProgress(Mob mob){
		
	}
	
	public boolean isComplete(){
		if(progression.isEmpty())
			return false;
		
		for(Objective objective: getQuest().getObjectives()){
			
			if(!progression.containsKey(objective)){
				return false;
			}
			
			if(objective.getAmmount() != progression.get(objective)){
				return false;
			}
		}
		return true;
	}
	
	public boolean endQuest(Player player, Mob mob){
		
		if(player == null) return false;
		Client client = player.getClient();
		if(client == null) return false;
		
		for(Reward reward: quest.getRewards()){
			if(reward instanceof ExperienceReward){
				player.setTotalExp(player.getTotalExp()+reward.getAmmount());
				player.setLevelUpExp(player.getLevelUpExp()-reward.getAmmount());
				client.sendPacket(Type.SAY, "Quest experience : "+reward.getAmmount());
			} else {
				Item item = ItemFactory.create(reward.getId());
				if(item == null) return false;
				
				InventoryItem inventoryItem = player.getInventory().storeItem(item);
				
				player.getPosition().getLocalMap().createEntityId(item);
				if(reward instanceof LimeReward){
					item.setExtraStats(reward.getAmmount());
				}
				client.sendPacket(Type.PICKUP, player);
				client.sendPacket(Type.PICK, inventoryItem);
			}
		}
		
		client.sendPacket(Type.SAY, "Quest completed");
		client.sendPacket(Type.QT, "eff "+mob.getPosition().getX()+" "+mob.getPosition().getY()+" "+player.getEntityId());
		player.setQuest(null);
		return true;
	}
}

