package com.googlecode.reunion.jreunion.game.skills.aidia;


import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class Recovery extends Skill implements Castable, Effectable {

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
	public boolean cast(LivingObject caster, LivingObject target) {
		if(caster == target && caster instanceof Player) {
			Player player = (Player)caster;
			int level = player.getSkillLevel(this);
			int playerHp = player.getHp();
			if(playerHp < player.getMaxHp()) {
				int mana = 10 + (int)((level-1) * ((double)21 / (double)25)); //mana usage
				int hp = 40 + (int)((level-1) * 2.5f); //hp recovery
				int playerMana = player.getMana();
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
