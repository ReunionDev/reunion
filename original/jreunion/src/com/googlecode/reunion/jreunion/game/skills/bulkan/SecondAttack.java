package com.googlecode.reunion.jreunion.game.skills.bulkan;

import java.util.List;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.Axe;
import com.googlecode.reunion.jreunion.game.items.equipment.SlayerWeapon;
import com.googlecode.reunion.jreunion.game.npc.Mob;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;

public class SecondAttack extends Skill {
	
	
	public SecondAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 149+skillLevel;
	}
	
	public float getDamageModifier(){
		/*
		 * lvl 1 = 20%
		 * lvl 2 = 40%
		 * lvl 3 = 60%
		 * 
		 * lvl 25 = 500%
		 */
		
		return 4.8f/(getMaxLevel()-1);		
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
		
		//Item<?> weapon = player.getEquipment().getMainHand();
		
		//if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
		int level = player.getSkillLevel(this);
		if(level>0){
			modifier += (0.2+((level-1)*getDamageModifier()));			
		}				
		//}
		
		return modifier;
	}
	
	public boolean cast(LivingObject caster, List<LivingObject> targets){
		
		Player player = null;
		
		if(caster instanceof Player){
			player = (Player)caster;
		}
		
		Item<?> shoulderMount = player.getEquipment().getShoulderMount();
		shoulderMount.use(caster);
		
		SlayerWeapon slayerWeapon = null;
		
		if(shoulderMount.getType() instanceof SlayerWeapon)
			slayerWeapon = (SlayerWeapon) shoulderMount.getType();
		
		long bestAttack = player.getBestAttack();
		long slayerDmg = slayerWeapon.getDamage();
		float slayerMemoryDmg = slayerWeapon.getMemoryDmg();
		float skillDmg = getDamageModifier(); 
		float slayerDemolitionDmg = slayerWeapon.getMemoryDmg();
		
		long damage = bestAttack + slayerDmg + (long)(bestAttack*slayerMemoryDmg*skillDmg);
		damage += (long)(damage*slayerDemolitionDmg);
		
		synchronized(targets){
			for(LivingObject target : targets){ 
				long newHp = Tools.between(target.getHp() - damage, 0l, target.getMaxHp());				
				
				if (newHp <= 0) {
					Logger.getLogger(LivingObject.class).info("Player "+player+" killed npc "+this);
					if(target instanceof Mob){
						((Mob)target).kill(player);
					}
				} else {
					target.setHp(newHp);
				}
				player.getClient().sendPacket(Type.SAV, target, shoulderMount);
			}
		}
		
		player.clearAttackQueue();
		
		
		return true;
	}
}
