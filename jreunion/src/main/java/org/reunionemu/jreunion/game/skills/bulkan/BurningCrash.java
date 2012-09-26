package org.reunionemu.jreunion.game.skills.bulkan;


import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.DemolitionWeapon;
import org.reunionemu.jreunion.game.items.equipment.SlayerWeapon;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.LocalMap;
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

	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments){

		int castStep = Integer.parseInt(arguments[4]);
		
		if(castStep == 255)
			return true;
		
		Player player = null;

		if(caster instanceof Player){
			player = (Player)caster;
		}

		Item<?> shoulderMount = player.getEquipment().getShoulderMount();
		if(!shoulderMount.use(caster, -1, 0)){
			return false;
		}

		SlayerWeapon slayerWeapon = null;

		if(shoulderMount.getType() instanceof SlayerWeapon)
			slayerWeapon = (SlayerWeapon) shoulderMount.getType();

		long bestAttack = player.getBestAttack();
		long slayerDmg = slayerWeapon.getDamage();
		float slayerMemoryDmg = slayerWeapon.getMemoryDmg();
		float skillDmg = getDamageModifier(player); 
		float slayerDemolitionDmg = slayerWeapon.getDemolition();
		float criticalMultiplier = slayerWeapon.getCritical();

		long damage = bestAttack + slayerDmg + (long)(bestAttack*slayerMemoryDmg);
		damage = damage + (long)(damage*criticalMultiplier); //add critical damage
		damage = damage + (long)(damage*skillDmg);	//add skill damage increase
		damage = damage + (long)(damage*slayerDemolitionDmg); //add slayer demolition damage

		player.setDmgType(slayerWeapon instanceof DemolitionWeapon ? 2 : (criticalMultiplier > 0 ? 1 : 0));
		
		synchronized (victim) {
			victim.getsAttacked(player, damage, false);

			int unknown = victim.getHp() <= 0 ? -1 : castStep == 1 ? 1 : 0;

			player.getClient().sendPacket(Type.SAV, null, -1, -1, shoulderMount.getExtraStats(), 3);
			if (unknown != 0) {
				player.getClient().sendPacket(Type.EFFECT, player, victim, this, 0, unknown, 30);
			}
			player.getClient().sendPacket(Type.AV, victim, player.getDmgType());

			if (victim.getHp() <= 0) {
				player.clearAttackQueue();
			}
		}
		return true;
	}
	
	public void effect(LivingObject source, LivingObject target, String[] arguments){
		
		if(source == null || arguments == null)
			return;
		
		int castStep = Integer.parseInt(arguments[4]);
		
		if(castStep == 255)
			return;
		
		int unknown = target.getHp() <= 0 ? -1 : 1;
		if(Integer.parseInt(arguments[4])==1 || target.getHp()==0){
			source.getInterested().sendPacket(Type.EFFECT, source, target , this, 0, unknown, 30);
		}
	}
	
	@Override
	public int getEffectModifier() {
		return 0;
	}
	
	@Override
	public List<LivingObject> getTargets(String[] arguments, LocalMap map){
		List<LivingObject> targets = new Vector<LivingObject>();
		targets.add(getSingleTarget(Integer.parseInt(arguments[3]), map));
		return targets;
	}
	
}