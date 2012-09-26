package org.reunionemu.jreunion.game.skills.kailipton;


import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.KailiptonPlayer;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;


public class ReturnToVillage extends Skill implements Castable {

	public ReturnToVillage(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 34 + skillLevel;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
	public float getSuccessRateModifier(){
		/* level 1 = 20%
		 * level 2 = 40%
		 * ...
		 * level 5 = 100%
		 */
		
		return 0.2f;
		
	}
	
	public float getSuccessRateModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.2f+((level-1)*getSuccessRateModifier()));			
		}	
		
		return modifier;
	}
	
	//Teleports player to town.
	//If there isn't enough mana then the client wont allow to use the skill.
	@Override
	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments) {
		if(caster instanceof KailiptonPlayer){
			Player player = (Player)caster;
			int manaSpent = 10; //mana spent is the same in every skill level
			
			player.setMana(player.getMana() - manaSpent);
			
			// calculate Success Rate of skill
			if(!Tools.successRateCalc(getSuccessRateModifier((Player)caster))){
				player.getClient().sendPacket(Type.SAY, "Failed to Return to Village.");
				return false;
			}
			
			player.spawn();
			return true;
		}
		return false;
	}
}