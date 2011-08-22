package com.googlecode.reunion.jreunion.game.skills.human;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.items.equipment.GunWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.skills.Modifier;
import com.googlecode.reunion.jreunion.game.skills.Modifier.ModifierType;
import com.googlecode.reunion.jreunion.game.skills.Modifier.ValueType;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;

public class SemiAutomatic extends Skill implements Modifier{

	public SemiAutomatic(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 4+skillLevel;
	}

	public Class<?> getWeaponType() {
		return GunWeapon.class;
	}
	
	public boolean getCondition(LivingObject owner){
		
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			Weapon weapon= player.getEquipment().getMainHand();
			return weapon!=null && getWeaponType().isInstance(weapon);			
		}		
		return false;		
	}
	
	public float getSuccessRateModifier(Player player){
		
		float modifier = 1;
		float successRate = 0;
	
		Weapon weapon = player.getEquipment().getMainHand();
		
		if(weapon!=null&&getWeaponType().isInstance(weapon)){
		
			int level = player.getSkillLevel(this);
			if(level>0){
				successRate += (0.05+((level-1)*getSuccessRateModifier()));
				if(Tools.successRateCalc(successRate)){//success rate of multi_shot
					//here we test if the success rate is above 50%.
					//if true, then player fires 3 shots, if not the player fire2 only 2 shots.
					modifier = (successRate > 0.5 && Tools.successRateCalc(0.5f)) ? 2 : 3;
					player.getClient().sendPacket(Type.MULTI_SHOT, "me", (int)modifier);
				}
			}				
		}
		
		return modifier;
	}
	public float getSuccessRateModifier(){
		/*
		 * lvl 1 = 5%
		 * lvl 2 = %
		 * 
		 * 
		 * lvl 25 = 95%
		 */
		
		return 0.9f/(getMaxLevel()-1);		
		
	}
	
	@Override
	public ValueType getValueType() {
		return Modifier.ValueType.DAMAGE;
		
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.MULTIPLICATIVE;
	}

	private int [] affectedSkillIds = {0};
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
		return getSuccessRateModifier((Player)livingObject);
	}
}
