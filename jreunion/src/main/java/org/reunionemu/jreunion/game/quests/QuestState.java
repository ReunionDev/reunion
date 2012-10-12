package org.reunionemu.jreunion.game.quests;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.reunionemu.jreunion.game.InventoryItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.Reward;
import org.reunionemu.jreunion.model.quests.objectives.CounterObjectiveState;
import org.reunionemu.jreunion.model.quests.objectives.MobObjective;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;
import org.reunionemu.jreunion.model.quests.objectives.PointsObjective;
import org.reunionemu.jreunion.model.quests.rewards.ExperienceReward;
import org.reunionemu.jreunion.model.quests.rewards.ItemReward;
import org.reunionemu.jreunion.model.quests.rewards.LimeReward;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class QuestState {
	
	Quest quest;
	Map<Objective, ObjectiveState> progression = new LinkedHashMap <Objective, ObjectiveState>();
	
	public QuestState(Quest quest) {
		this.quest = quest;
		for(Objective objective: quest.getObjectives()){
			progression.put(objective, objective.createObjectiveState());
		}
	}
	
	public ObjectiveState getObjectiveState(Objective objective){
		return progression.get(objective);
	}
		
	public Quest getQuest(){
		return this.quest;
	}
	
	/*
	public int getProgression(int id){
		
		for( Objective objective: progression.keySet()){
			//LoggerFactory.getLogger(QuestState.class).debug("OBJECTIVE ID: "+objective.getId());
			if(objective.getId() == id)
				return getProgression(objective);
		}
		
		return 0;
	}*/
	
	public void handleProgress(Npc<?> mob, Player player){
		for(Objective objective: getQuest().getObjectives()){
			if(objective instanceof MobObjective){
				handleMobProgress((MobObjective) objective, mob, player);
			}
			else if(objective instanceof PointsObjective){
					handlePointsProgress(mob);
			}
			
		}
	}
	
	public int getObjectiveId(Objective objective){
		Iterator<Objective> iter = progression.keySet().iterator();
		int count = 0;
		while(iter.hasNext()){
			Objective obj = iter.next();
			if(obj==objective){
				return count; 
			}
			count ++;
		}
		throw new RuntimeException("Objective not found");		
		
	}
	
	public void handleMobProgress(MobObjective objective, Npc<?> mob, Player player){
		
		
		int objectiveMobType = objective.getType();		
		
		if(objectiveMobType == mob.getType().getTypeId()){
			
			CounterObjectiveState state = (CounterObjectiveState) getObjectiveState(objective);
			
			state.decrease();
			
			if(!state.isComplete()){
				
				Client client = player.getClient();
				if (client == null) return;	

				int slot = getObjectiveId(objective);
				int ammountRemaining = state.getCounter();
				
				if(!(quest instanceof ExperienceQuest)){
					client.sendPacket(Type.QT, "kill " + slot + " " + state.getCounter());
				}
				
				if(quest instanceof ExperienceQuest){
					if(ammountRemaining == 1)
						client.sendPacket(Type.INFO, "Boss is near!");
				}
				
				
			}
			if(isComplete()){
				endQuest(player, mob);
			}
		}
	}
	
	public void handlePointsProgress(Npc<?> mob){
		
	}
	
	public boolean isComplete(){
		
		for(Objective objective: progression.keySet()){
			
			ObjectiveState state = getObjectiveState(objective);
			if(!state.isComplete()){
				return false;
			}
		}
		return true;
	}
	
	public boolean endQuest(Player player, Npc<?> mob){
		
		if(player == null) return false;
		Client client = player.getClient();
		if(client == null) return false;
		
		ItemManager itemManager = client.getWorld().getItemManager();
		
		for(Reward reward: quest.getRewards()){
			if(reward instanceof ExperienceReward){
				player.setTotalExp(player.getTotalExp()+((ExperienceReward)reward).getExperience());
				player.setLevelUpExp(player.getLevelUpExp()-((ExperienceReward)reward).getExperience());
				client.sendPacket(Type.SAY, "Quest experience : "+((ExperienceReward)reward).getExperience());
			} else if(reward instanceof ItemReward) {
				for(int i = 0; i < ((ItemReward)reward).getAmount();i++){
					Item<?> item = itemManager.create(((ItemReward)reward).getType());
					if(item == null) {
						continue;
					}
					
					InventoryItem inventoryItem = player.getInventory().storeItem(item, -1);
					
					player.getPosition().getLocalMap().createEntityId(item);
	
					client.sendPacket(Type.PICKUP, player);
					client.sendPacket(Type.PICK, inventoryItem);	
				}
			}
				
			if(reward instanceof LimeReward){
				player.addLime(((LimeReward)reward).getLime());
			}
			
		}
		
		client.sendPacket(Type.SAY, "Quest completed");
		client.sendPacket(Type.QT, "eff "+mob.getPosition().getX()+" "+mob.getPosition().getY()+" "+player.getEntityId());
		player.setQuest(null);
		return true;
	}
}

