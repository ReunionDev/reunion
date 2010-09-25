package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Skill;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class CharSkill {

	List<Skill> skillList = new Vector<Skill>();

	public CharSkill() {
		super();
		// loadSkillList(race);

	}

	public void addSkill(Skill skill) {
		if (skillList.contains(skill)) {
			return;
		}
		skillList.add(skill);
	}

	public Skill getSkill(int id) {
		Iterator<Skill> iter = getSkillListIterator();

		while (iter.hasNext()) {
			Skill skill = iter.next();
			if (skill.getId() == id) {
				return skill;
			}
		}
		return null;
	}

	public int getSkillIndex(int id) {
		Iterator<Skill> iter = getSkillListIterator();
		int count = 0;

		while (iter.hasNext()) {
			Skill skill = iter.next();
			if (skill.getId() == id) {
				return count;
			}
			count++;
		}
		return 0;
	}

	public Iterator<Skill> getSkillListIterator() {
		return skillList.iterator();
	}

	public void incSkill(Player player, Skill skill) {
		// G_Skill skill = getSkill(id);

		// if(skill.getCurrLevel()==0){
		// skill.setDmg(skill.getStartAmmount());
		// }
		// Logger.getLogger(CharSkill.class).info("Skill lvl Before: "+skill.getLevel());
		if (skill == null) {
			return;
		}
		skill.setCurrLevel(skill.getCurrLevel() + 1);
		setSkill(player, skill);
		// skill.setDmg(skill.getCurrLevel()*skill.getIncAmmount());
	}

	public void loadSkillList(Race race) {
		switch (race) {
		case BULKAN: {
			addSkill(new Skill(1));
			addSkill(new Skill(2));
			addSkill(new Skill(17));
			addSkill(new Skill(18));
			addSkill(new Skill(19));
			addSkill(new Skill(31));
			addSkill(new Skill(37));
			addSkill(new Skill(38));
			addSkill(new Skill(39));
			addSkill(new Skill(40));
			addSkill(new Skill(41));
			addSkill(new Skill(60));
			addSkill(new Skill(61));
			addSkill(new Skill(71));
			addSkill(new Skill(75));
			break;
		}
		case KAILIPTON: {
			addSkill(new Skill(3));
			addSkill(new Skill(4));
			addSkill(new Skill(5));
			addSkill(new Skill(7));
			addSkill(new Skill(8));
			addSkill(new Skill(10));
			addSkill(new Skill(11));
			addSkill(new Skill(12));
			addSkill(new Skill(13));
			addSkill(new Skill(14));
			addSkill(new Skill(15));
			addSkill(new Skill(26));
			addSkill(new Skill(27));
			addSkill(new Skill(28));
			addSkill(new Skill(47));
			addSkill(new Skill(48));
			addSkill(new Skill(49));
			addSkill(new Skill(50));
			addSkill(new Skill(51));
			addSkill(new Skill(52));
			addSkill(new Skill(62));
			addSkill(new Skill(63));
			addSkill(new Skill(64));
			addSkill(new Skill(72));
			addSkill(new Skill(76));
			break;
		}
		case AIDIA: {
			addSkill(new Skill(6));
			addSkill(new Skill(22));
			addSkill(new Skill(24));
			addSkill(new Skill(32));
			addSkill(new Skill(33));
			addSkill(new Skill(34));
			addSkill(new Skill(53));
			addSkill(new Skill(54));
			addSkill(new Skill(55));
			addSkill(new Skill(56));
			addSkill(new Skill(57));
			addSkill(new Skill(58));
			addSkill(new Skill(67));
			addSkill(new Skill(68));
			addSkill(new Skill(69));
			addSkill(new Skill(70));
			addSkill(new Skill(73));
			addSkill(new Skill(78));
			break;
		}
		case HUMAN: {
			addSkill(new Skill(20));
			addSkill(new Skill(21));
			addSkill(new Skill(29));
			addSkill(new Skill(30));
			addSkill(new Skill(35));
			addSkill(new Skill(36));
			addSkill(new Skill(42));
			addSkill(new Skill(43));
			addSkill(new Skill(44));
			addSkill(new Skill(45));
			addSkill(new Skill(46));
			addSkill(new Skill(65));
			addSkill(new Skill(66));
			addSkill(new Skill(74));
			addSkill(new Skill(77));
			break;
		}
		case HYBRIDER: {
			addSkill(new Skill(79));
			addSkill(new Skill(80));
			addSkill(new Skill(81));
			addSkill(new Skill(82));
			addSkill(new Skill(83));
			addSkill(new Skill(84));
			addSkill(new Skill(85));
			addSkill(new Skill(86));
			addSkill(new Skill(87));
			addSkill(new Skill(88));
			addSkill(new Skill(89));
			addSkill(new Skill(90));
			addSkill(new Skill(91));
			addSkill(new Skill(92));
			addSkill(new Skill(93));
			addSkill(new Skill(94));
			addSkill(new Skill(95));
			addSkill(new Skill(96));
			addSkill(new Skill(97));
			addSkill(new Skill(98));
			break;
		}
		default:
			throw new RuntimeException("Invalid Race: "+race);
			
		}
	}

	public void removeSkill(Skill skill) {
		while (skillList.contains(skill)) {
			skillList.remove(skill);
		}
	}

	public void setSkill(Player player, Skill skill) {

		int index = getSkillIndex(skill.getId());
		// G_Skill skill = getSkill(id);

		// skill.setCurrLevel(skill.getCurrLevel());

		if (skill.getCurrLevel() == 1) {
			skill.setCurrFirstRange(skill.getMinFirstRange());
			skill.setCurrSecondRange(skill.getMinSecondRange());
			skill.setCurrConsumn(skill.getMinConsumn());
		} else if (skill.getCurrLevel() > 1) {
			float incFirstRange = (float) (skill.getCurrLevel() - 1)
					* (float) (skill.getMaxFirstRange() - skill
							.getMinFirstRange()) / skill.getMaxLevel();
			skill.setCurrFirstRange(Math.round(skill.getMinFirstRange()
					+ incFirstRange));

			float incSecondRange = (float) (skill.getCurrLevel() - 1)
					* (float) (skill.getMaxSecondRange() - skill
							.getMinSecondRange()) / skill.getMaxLevel();
			skill.setCurrSecondRange(Math.round(skill.getMinSecondRange()
					+ incSecondRange));

			float incConsumn = (float) (skill.getCurrLevel() - 1)
					* (float) (skill.getMaxConsumn() - skill.getMinConsumn())
					/ skill.getMaxLevel();
			skill.setCurrConsumn(Math.round(skill.getMinConsumn() + incConsumn));
		}

		// S_Server.getInstance().getWorldModule().getWorldCommand().serverSay("Consumn:"+skill.getCurrConsumn()+" FR:"+skill.getCurrFirstRange());

		skillList.set(index, skill);
	}
}
