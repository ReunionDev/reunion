package com.googlecode.reunion.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.StaffWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class Lightning extends Tier2 implements Castable, Modifier {

	public Lightning(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public int getMaxLevel() {
		return 25;
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.LIGHT;
	}

	public int getLevelRequirement(int level) {
		return 37+level;
	}
	
	public float getDamageModifier(){
		/* level 1 = 6 (magic damage)
		 * level 2 = 18
		 * level 3 = 21
		 * ...
		 * level 25 = 150
		 */
		
		return (float)144/(getMaxLevel()-1);
		
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (6+((level-1)*getDamageModifier()));			
			}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 10
		 * level 2 = 10
		 * level 3 = 11
		 * ...
		 * level 25 = 30
		 */
		return 20f/(getMaxLevel()-1);
	}
	
	float getManaModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getManaModifier()));			
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
			
			Weapon weapon = player.getEquipment().getMainHand();
			float baseDamage = player.getBaseDamage();
			float weaponDamage = 0;
			double weaponMagicBoost=1;
			
			if(weapon instanceof StaffWeapon){
				weaponDamage += weapon.getMinDamage() + 
						(Server.getRand().nextFloat()*(weapon.getMaxDamage()-weapon.getMinDamage()));
				weaponMagicBoost += weapon.getMagicDmg(); // % of magic dmg boost
			}
			
			float lightDamage = getDamageModifier(player);
			float lightningMasteryDamage = 1;
			
			// calculate damage for skills LightningBall and LightningMastery
			for(Skill skill: ((Player)caster).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // LightningMastery
									lightningMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE: // LightningBall
									lightDamage += (modifier.getModifier(caster)*0.6);
									break;
							}
						}
					}						
				}
			}
			
			float magicDamage = (float)((baseDamage + weaponDamage + lightDamage)
					* lightningMasteryDamage * weaponMagicBoost);
			
			synchronized(victims){
				for(LivingObject victim : victims){
					int newHp = victim.getHp() - (int) (magicDamage);				
					
					if (newHp <= 0) {
						((Mob)victim).kill(player);
					} else {
						victim.setHp(newHp);
					}
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
	
	private int [] affectedSkillIds = {27};
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