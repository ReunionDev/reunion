package org.reunionemu.jreunion.game.skills.bulkan;

import java.util.List;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.SlayerWeapon;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;

public class BurningCrash extends Skill implements Castable, Effectable{

	public BurningCrash(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 30;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 275;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}

	public long getLimeModifier(){
		/*
		 * lvl 1 = 40000
		 * lvl 2 = 50000
		 * lvl 3 = 60000
		 * lvl 4 = 70000
		 * 
		 * lvl 30 = 330000
		 */

		return (long)290000/(getMaxLevel()-1); 
	}
	
	public float getModifier(){
		/*
		 * lvl 1 = 20
		 * lvl 2 = 19
		 * lvl 3 = 19
		 * lvl 4 = 18
		 * 
		 * lvl 30 = 6
		 */

		return 14f/(getMaxLevel()-1); 
	}
	
	public float getDamageModifier(){
		/*
		 * lvl 1 = 250%
		 * lvl 2 = 276%
		 * lvl 3 = 308%
		 * lvl 4 = 346
		 * 
		 * lvl 30 = 3438%
		 */

		return 22.50f/(getMaxLevel()-1); 
	}

	public float getStaminaModifier(){
		/* stamina spent:
		 * level 1 = 1
		 * level 2 = 3
		 * level 3 = 5
		 * level 4 = 7
		 * ...
		 * level 30 = 65
		 */
		return 30f/(getMaxLevel()-1);
	}
	
	public float getDamageModifier(Player player){

		float modifier = 1;

		//Item<?> weapon = player.getEquipment().getMainHand();

		//if(weapon!=null&&getWeaponType().isInstance(weapon)){

		int level = player.getSkillLevel(this);
		if(level>0){
			modifier = (2.5f+((level-1)*getDamageModifier())); 
		} 

		return modifier;
	}

	public boolean cast(LivingObject caster, List<LivingObject> victims){

		Player player = null;

		if(caster instanceof Player){
			player = (Player)caster;
		}

		Item<?> shoulderMount = player.getEquipment().getShoulderMount();
		shoulderMount.use(caster, -1, 0);

		SlayerWeapon slayerWeapon = null;

		if(shoulderMount.getType() instanceof SlayerWeapon)
			slayerWeapon = (SlayerWeapon) shoulderMount.getType();

		long bestAttack = player.getBestAttack();
		long slayerDmg = slayerWeapon.getDamage();
		float slayerMemoryDmg = slayerWeapon.getMemoryDmg();
		float skillDmg = getDamageModifier(player); 
		float slayerDemolitionDmg = slayerWeapon.getDemolitionDmg();

		long damage = bestAttack + slayerDmg + (long)(bestAttack*slayerMemoryDmg*skillDmg);
		damage += (long)(damage*slayerDemolitionDmg);

		synchronized(victims){
			for(LivingObject victim : victims){
				if(victim.getPercentageHp() == 100){
					player.getClient().sendPacket(Type.EFFECT, player, victim , this, 0, 1, 30);
				}
				victim.getsAttacked(player, damage);
				player.getClient().sendPacket(Type.SAV, null,-1, -1, shoulderMount.getExtraStats(), 3);
				if(victim.getHp() == 0){
					player.getClient().sendPacket(Type.EFFECT, player, victim , this, 0, -1, 30);
				}
				player.getClient().sendPacket(Type.AV, victim, player.getDmgType());		
			}
		}

		player.clearAttackQueue();


		return true;
	}
	
	public void effect(LivingObject source, LivingObject target){
		if(target.getPercentageHp() == 100){
			source.getInterested().sendPacket(Type.EFFECT, source, target , this, 0, 1, 30);
		} else 	if(target.getHp() == 0){
			source.getInterested().sendPacket(Type.EFFECT, source, target , this, 0, -1, 30);
		}
	}
	
	@Override
	public int getEffectModifier() {
		return 0;
	}
	
	
}