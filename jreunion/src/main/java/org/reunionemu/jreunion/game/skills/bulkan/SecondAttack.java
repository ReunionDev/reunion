package org.reunionemu.jreunion.game.skills.bulkan;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.Axe;
import org.reunionemu.jreunion.game.items.equipment.DemolitionWeapon;
import org.reunionemu.jreunion.game.items.equipment.SlayerWeapon;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;

public class SecondAttack extends Skill{
	
	
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
	
	@Override
	public int getAffectedTargets() {
		return 1;
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
	
	public boolean cast(LivingObject caster, List<LivingObject> victims, int unknown1){
		Player player = null;
		
		if(caster instanceof Player){
			player = (Player)caster;
		}
		
		Item<?> shoulderMount = player.getEquipment().getShoulderMount();
		shoulderMount.use(caster, -1, 0);
		
		SlayerWeapon slayerWeapon = null;
		
		if(shoulderMount!=null && shoulderMount.getType() instanceof SlayerWeapon)
			slayerWeapon = (SlayerWeapon) shoulderMount.getType();
		
		long bestAttack = player.getBestAttack();
		long slayerDmg = slayerWeapon.getDamage();
		float slayerMemoryDmg = slayerWeapon.getMemoryDmg();
		float skillDmg = getDamageModifier(); 
		float slayerDemolitionDmg = slayerWeapon.getDemolitionDmg();
		float criticalMultiplier = slayerWeapon.getCritical();
		
		long damage = bestAttack + slayerDmg + (long)(bestAttack*slayerMemoryDmg*skillDmg);
		damage += (long)(damage*criticalMultiplier);
		damage += (long)(damage*slayerDemolitionDmg);
		
		player.setDmgType(slayerWeapon instanceof DemolitionWeapon ? 2 : (criticalMultiplier > 0 ? 1 : 0));
		
		synchronized(victims){
			for(LivingObject victim : victims){
				victim.getsAttacked(player, damage, false);
				player.getClient().sendPacket(Type.SAV, victim,	player.getDmgType(), 0,
						shoulderMount.getExtraStats(), 3);
				player.getInterested().sendPacket(Type.SECONDATTACK, player, victim, getId());
			}
		}
		
		player.clearAttackQueue();
		
		
		return true;
	}
}
