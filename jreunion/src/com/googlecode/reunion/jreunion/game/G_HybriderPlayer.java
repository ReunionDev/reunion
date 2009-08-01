package com.googlecode.reunion.jreunion.game;
import com.googlecode.reunion.jreunion.server.S_Client;
import com.googlecode.reunion.jreunion.server.S_Server;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_HybriderPlayer extends G_Player {
	
	public G_HybriderPlayer() {
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
		
		int newHp = mob.getCurrHp() - getBaseDmg(this);
		
		if(newHp <= 0){
			
			mob.setDead();
			
			this.updateStatus(12,this.getLvlUpExp() - mob.getExp(),0);
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
		int randDmg, baseDmg=0;
				
		randDmg = player.getMinDmg() + (int)(Math.random()*(player.getMaxDmg()-player.getMinDmg()));
				
		baseDmg = (int)(randDmg + this.getLevel()/6 + this.getStr()/4 + this.getDex()/4 + this.getCons()/8);
		
		return baseDmg;
	}
	
	public void useSkill(G_LivingObject livingObject, int skillId){
		
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