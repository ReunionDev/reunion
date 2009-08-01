package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.*;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_KailiptonPlayer extends G_Player {
	
	public G_KailiptonPlayer() {
		super();
	}
	
	public void meleeAttack(G_LivingObject livingObject){
		if(livingObject instanceof G_Mob)
			this.meleeAttackMob((G_Mob)livingObject);
		else
			if(livingObject instanceof G_Player)
				this.meleeAttackPlayer((G_Player)livingObject);
	}
	
	private void meleeAttackMob(G_Mob mob) {
		int newHp;
		
		newHp = mob.getCurrHp() - getBaseDmg(this);
						
		if(newHp <= 0){
			
			mob.setDead();
			
			this.updateStatus(12,this.getLvlUpExp()-mob.getExp(),0);
			this.updateStatus(11,mob.getExp(),0);
			this.updateStatus(10,mob.getLime(),0);
			
			if(mob.getType() == 324){
				G_Item item = com.googlecode.reunion.jreunion.server.S_ItemFactory.createItem(1054); 
				
				item.setExtraStats((int)(Math.random()*10000));
				
				this.pickupItem(item.getEntityId());
				this.getQuest().questEnd(this,669);
				this.getQuest().questEff(this);
			}
		}
		else
			mob.setCurrHp(newHp);
	}
	
	private void meleeAttackPlayer(G_Player player) {
	
	}
	
	public int getBaseDmg(G_Player player) {
		int baseDmg, randDmg;
		
		randDmg = player.getMinDmg() + (int)(Math.random()*(player.getMaxDmg()-player.getMinDmg()));
		
		baseDmg = (int)(randDmg + this.getLevel()/5 + this.getWis()/2);
		
		return baseDmg;
	}
		
	public void useSkill(G_LivingObject livingObject, int skillId){
		
		G_Skill skill = this.getCharSkill().getSkill(skillId);
		
		if(skill.getType() == 0)
			permanentSkill(skill);
		else if(skill.getType() == 1)
			activationSkill(skill);
		else if(skill.getType() == 2)
			attackSkill(livingObject, skill);
	}
	
	public void activationSkill(G_Skill skill){
		
	}

	public void attackSkill(G_LivingObject livingObject, G_Skill skill){
		if(livingObject instanceof G_Mob)
			skillAttackMob((G_Mob)livingObject, skill);
		else if(livingObject instanceof G_Player)
			skillAttackPlayer((G_Player)livingObject, skill);
	}
	
	public void permanentSkill(G_Skill skill){
	
	}
	
	public void skillAttackMob(G_Mob mob, G_Skill skill){
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client == null)
			return;
				
		int newHp = mob.getCurrHp() - (int)skill.getCurrFirstRange();
				
		this.updateStatus(skill.getStatusUsed(),this.getCurrMana() - (int)skill.getCurrConsumn(),this.getMaxMana());
				
		if(newHp <= 0){
			
			mob.setDead();
			
			this.updateStatus(12,this.getLvlUpExp()-mob.getExp(),0);
			this.updateStatus(11,mob.getExp(),0);
			this.updateStatus(10,mob.getLime(),0);
		}
		else
			mob.setCurrHp(newHp);
		
		int percentageHp = (mob.getCurrHp()*100)/mob.getMaxHp();
		
		if(percentageHp == 0 && mob.getCurrHp() > 0)
			percentageHp = 1;
					
		String packetData = "attack_vital npc " + mob.getEntityId()+
			" " + percentageHp + " 0 0\n";
		
		//S> attack_vital npc [NpcID] [RemainHP%] 0 0
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
		
		if (this.getSession().getPlayerListSize() > 0){
			Iterator<G_Player> playerIter = this.getSession().getPlayerListIterator();
		
			while(playerIter.hasNext()){
				G_Player pl = (G_Player)playerIter.next();
				
				client = S_Server.getInstance().getNetworkModule().getClient(pl);
				
				if(client == null)
					continue;
				
				packetData = "effect "+skill.getId()+" char "+this.getEntityId()+" npc "+mob.getEntityId()+
					" "+percentageHp+" 0 0\n";
								
				// S> effect [SkillID] char [charID] npc [npcID] [RemainNpcHP%] 0 0
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);				
			}
		}
	}
	
	public void skillAttackPlayer(G_Player player, G_Skill skill){
		
	}
	
	public void levelUpSkill(G_Skill skill){
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(this);
		
		if(client==null)
			return;
		
		String packetData = new String();
		
		getCharSkill().incSkill(this,skill); 
		packetData = "skilllevel "+ skill.getId() +" "+ skill.getCurrLevel()+"\n";
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
	}
}