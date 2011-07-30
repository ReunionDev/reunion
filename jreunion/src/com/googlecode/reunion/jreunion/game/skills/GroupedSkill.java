package com.googlecode.reunion.jreunion.game.skills;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;

public abstract class GroupedSkill extends Skill{
	public GroupedSkill(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public abstract int [] getSkillsInGroup();
	
	@Override
	public boolean levelUp(Player player) {
		for(int skillObject:getSkillsInGroup()){
			
			GroupedSkill skill = (GroupedSkill)player.getSkill(skillObject);
			if(skill==null||!skill.groupUp(player))
				return false;
		}
		
		return true;
		
	}
	
	private boolean groupUp(Player player){
		return super.levelUp(player);
	}
}
