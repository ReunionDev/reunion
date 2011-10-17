package com.googlecode.reunion.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.StaffWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class FireBall extends Tier1 implements Castable, Modifier {

	public FireBall(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.FIRE;
	}
	
	public float getDamageModifier(){
		/* level 1 = 15 (magic damage)
		 * level 2 = 18
		 * level 3 = 21
		 * ...
		 * level 25 = 95
		 */
		
		return (float)80/(getMaxLevel()-1);
		
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (15 + ((level-1)*getDamageModifier()));			
		}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 5
		 * level 2 = 5
		 * level 3 = 6
		 * ...
		 * level 25 = 25
		 */
		return 20f/(getMaxLevel()-1);
	}
	
	float getManaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (5 + ((level-1) * getManaModifier()));			
		}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
		if(caster instanceof KailiptonPlayer){
			Player player = (Player)caster;
			int currentMana = player.getMana();
			int manaSpent = (int) getManaModifier(player);
			
			player.setMana(currentMana - manaSpent);
			
			Item<?> item = player.getEquipment().getMainHand();
			float baseDamage = player.getBaseDamage();
			float weaponDamage = 0;
			double weaponMagicBoost=1;
			Weapon weapon = null;
			
			if(item.is(StaffWeapon.class)){
				weapon = (Weapon)item.getType();
				weaponDamage += weapon.getMinDamage(item) + 
						(Server.getRand().nextFloat()*(weapon.getMaxDamage(item)
								- weapon.getMinDamage(item)));
				weaponMagicBoost += weapon.getMagicDmg(item); // % of magic dmg boost
			}
			
			float fireDamage = getDamageModifier(player);
			float fireMasteryDamage = 1;
			
			// calculate damage from skill FireMastery
			for(Skill skill: player.getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // FireMastery
									fireMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE:
									fireDamage += modifier.getModifier(caster);
									break;
							}
						}
					}						
				}
			}
			
			float magicDamage = (float)((baseDamage + weaponDamage + fireDamage) * fireMasteryDamage * weaponMagicBoost);
			
			//This skill can target up to 2 targets
			//(Both targets receive 100% dmg)
			synchronized(victims){
				for(LivingObject victim : victims){ 
					victim.getsAttacked(player, (int)magicDamage);
				}
				return true;
			}
		}		
		return false;
	}
	
	public boolean getCondition(LivingObject owner){
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			return true;
		}
		return false;
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.ADDITIVE;
	}
	
	private int [] affectedSkillIds = {10, 26};
	private List<Skill>  affectedSkills = null ;
	
	@Override
	public List<Skill> getAffectedSkills() {
		synchronized(affectedSkillIds){
			if (affectedSkills==null){
				affectedSkills = new Vector<Skill>();
				for(int skillId:affectedSkillIds){					
					SkillManager skillManager = getSkillManager();
					affectedSkills.add(skillManager.getSkill(skillId));					
				}
			}		
		}		
		return affectedSkills;
	}

	@Override
	public float getModifier(LivingObject livingObject) {
		return getDamageModifier((Player)livingObject);
	}

}