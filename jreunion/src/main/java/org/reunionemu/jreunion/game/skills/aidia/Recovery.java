package org.reunionemu.jreunion.game.skills.aidia;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;
public class Recovery extends Skill implements Castable{

	public Recovery(SkillManager skillManager,int id) {
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
	
	public long getManaModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getManaModifier()));			
		}	
		
		return modifier;
	}
	
	public float getHpModifier(){
		/* mana spent:
		 * level 1 = 40
		 * level 2 = 42
		 * level 3 = 45
		 * ...
		 * level 25 = 100
		 */
		return 60f/(getMaxLevel()-1);
	}
	
	long getHpModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (40 + ((level-1) * getManaModifier()));			
		}	
		
		return modifier;
	}

	//when casted, this skill will only send to the client
	//the new hp and mana values.
	@Override
	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments) {

			if(caster instanceof Player) {
				Player player = (Player)caster;
				long playerHp = player.getHp();
				if(playerHp < player.getMaxHp()) {
					long mana = getManaModifier(player); //mana usage
					long hp = getHpModifier(player); //hp recovery
					long playerMana = player.getMana();
					if(playerMana >= mana) {
						player.setMana(playerMana - mana);
						player.setHp(playerHp + hp);
						return true;
					}
				}
				return true;
			}
		return false;
	}
}
