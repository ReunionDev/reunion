package org.reunionemu.jreunion.game.skills;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;

public abstract class GroupedSkill extends Skill{
	public GroupedSkill(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public abstract int [] getSkillsInGroup();
	
	@Override
	public void reset(Player player){
		
		for(int skillObject:getSkillsInGroup()){
			GroupedSkill skill = (GroupedSkill)player.getSkill(skillObject);
			skill.groupReset(player);
		}			
	}
	
	private void groupReset(Player player){
		super.reset(player);
	}
	
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
