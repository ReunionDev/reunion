package com.googlecode.reunion.jreunion.game.skills.kailipton;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.KailiptonPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;


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
	
	public float getSuccessRateModifier(){
		/* level 1 = 20%
		 * level 2 = 40%
		 * ...
		 * level 5 = 100%
		 */
		
		return (float)0.2;
		
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
	public boolean cast(LivingObject caster, LivingObject... targets) {
		if(caster instanceof KailiptonPlayer){
			int manaSpent = 10;
			// calculate Success Rate of skill
			if(!Tools.successRateCalc(getSuccessRateModifier((Player)caster))){
				((KailiptonPlayer) caster).setMana(((KailiptonPlayer) caster).getMana() - manaSpent);
				((KailiptonPlayer) caster).getClient().sendPacket(Type.SAY, "Failed to cast the skill.");
				return false;
			}
			Map map = caster.getPosition().getMap();
			
			switch(map.getId()){
				case 4: { //Laglamia
					Position townPosition = new Position(7026,5220,106,caster.getPosition().getLocalMap(),0.0f);			
					((KailiptonPlayer) caster).getClient().getWorld().getCommand().GoToPos((Player)caster, townPosition);
					((KailiptonPlayer) caster).setMana(((KailiptonPlayer) caster).getMana() - manaSpent);
					return true;
				}
				default: {// unknown return map location
					((KailiptonPlayer) caster).getClient().sendPacket(Type.SAY, "Return to Village location, not set yet to this map.");
					return false;
				}
			}
		}
		return false;
	}
}