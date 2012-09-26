package org.reunionemu.jreunion.game.skills.bulkan;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.SkillManager;

public abstract class WeaponMastery extends Skill implements Modifier {
	
	
	public WeaponMastery(SkillManager skillManager,int id) {
		super(skillManager, id);
	}
	
	public abstract Class<?> getWeaponType();
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
	public float getDamageModifier(Player player){
		
		float modifier = 1;
	
		Item<?> weapon = player.getEquipment().getMainHand();
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			if(level>0){
				modifier += (0.1+((level-1)*getDamageModifier()));			
			}				
		}
	
		return modifier;
	}
	public float getDamageModifier(){
		/*
		 * lvl 1 = 10%
		 * lvl 2 = 17%
		 * 
		 * 
		 * lvl 25 = 200%
		 * 
		 * 1.90 = 200% - 10%
		 * 24 = m
		 * 
		 * 
		 * 200% = 25
		 * ?    = 1
		 * 
		 */
		/*double percent = 0;
		
		for(int i = 1; i <= getMaxLevel(); i++)
			percent += 10 - ((i == 1) ? 0 : ((i == 2) ? 3 : ((i == 14) ? 3 : 2)));
		return ((percent/100)+1);*/
		
		return 1.90f/(getMaxLevel()-1);	
		//f l2 1.90 / (2-1)
		
	}
	
	public boolean getCondition(LivingObject owner){
		
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			Item<?> weapon= player.getEquipment().getMainHand();
			return weapon!=null && getWeaponType().isInstance(weapon);			
		}		
		return false;		
	}
	
	@Override
	public int getLevelRequirement(int level) {
		return 4+level;
	}	
	
	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public ValueType getValueType() {
		return Modifier.ValueType.DAMAGE;
		
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.MULTIPLICATIVE;
	}

	private int [] affectedSkillIds = {0, 18, 31};
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
