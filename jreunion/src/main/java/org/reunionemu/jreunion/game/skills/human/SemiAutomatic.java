package org.reunionemu.jreunion.game.skills.human;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.GunWeapon;
import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;

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
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}

	public Class<?> getWeaponType() {
		return GunWeapon.class;
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
	
	public float getSuccessRateModifier(Player player){
		
		float modifier = 1;
		float successRate = 0;
	
		Item<?> weapon = player.getEquipment().getMainHand();
		
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
