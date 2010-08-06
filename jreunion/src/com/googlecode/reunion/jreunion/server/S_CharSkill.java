package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_Skill;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_CharSkill {

	List<G_Skill> skillList = new Vector<G_Skill>();

	public S_CharSkill() {
		super();
		// loadSkillList(race);

	}

	public void addSkill(G_Skill skill) {
		if (skillList.contains(skill)) {
			return;
		}
		skillList.add(skill);
	}

	public G_Skill getSkill(int id) {
		Iterator<G_Skill> iter = getSkillListIterator();

		while (iter.hasNext()) {
			G_Skill skill = iter.next();
			if (skill.getId() == id) {
				return skill;
			}
		}
		return null;
	}

	public int getSkillIndex(int id) {
		Iterator<G_Skill> iter = getSkillListIterator();
		int count = 0;

		while (iter.hasNext()) {
			G_Skill skill = iter.next();
			if (skill.getId() == id) {
				return count;
			}
			count++;
		}
		return 0;
	}

	public Iterator<G_Skill> getSkillListIterator() {
		return skillList.iterator();
	}

	public void incSkill(G_Player player, G_Skill skill) {
		// G_Skill skill = getSkill(id);

		// if(skill.getCurrLevel()==0){
		// skill.setDmg(skill.getStartAmmount());
		// }
		// System.out.println("Skill lvl Before: "+skill.getLevel());
		if (skill == null) {
			return;
		}
		skill.setCurrLevel(skill.getCurrLevel() + 1);
		setSkill(player, skill);
		// skill.setDmg(skill.getCurrLevel()*skill.getIncAmmount());
	}

	public void loadSkillList(int race) {
		switch (race) {
		case 0: {
			addSkill(new G_Skill(1));
			addSkill(new G_Skill(2));
			addSkill(new G_Skill(17));
			addSkill(new G_Skill(18));
			addSkill(new G_Skill(19));
			addSkill(new G_Skill(31));
			addSkill(new G_Skill(37));
			addSkill(new G_Skill(38));
			addSkill(new G_Skill(39));
			addSkill(new G_Skill(40));
			addSkill(new G_Skill(41));
			addSkill(new G_Skill(60));
			addSkill(new G_Skill(61));
			addSkill(new G_Skill(71));
			addSkill(new G_Skill(75));
			break;
		}
		case 1: {
			addSkill(new G_Skill(3));
			addSkill(new G_Skill(4));
			addSkill(new G_Skill(5));
			addSkill(new G_Skill(7));
			addSkill(new G_Skill(8));
			addSkill(new G_Skill(10));
			addSkill(new G_Skill(11));
			addSkill(new G_Skill(12));
			addSkill(new G_Skill(13));
			addSkill(new G_Skill(14));
			addSkill(new G_Skill(15));
			addSkill(new G_Skill(26));
			addSkill(new G_Skill(27));
			addSkill(new G_Skill(28));
			addSkill(new G_Skill(47));
			addSkill(new G_Skill(48));
			addSkill(new G_Skill(49));
			addSkill(new G_Skill(50));
			addSkill(new G_Skill(51));
			addSkill(new G_Skill(52));
			addSkill(new G_Skill(62));
			addSkill(new G_Skill(63));
			addSkill(new G_Skill(64));
			addSkill(new G_Skill(72));
			addSkill(new G_Skill(76));
			break;
		}
		case 2: {
			addSkill(new G_Skill(6));
			addSkill(new G_Skill(22));
			addSkill(new G_Skill(24));
			addSkill(new G_Skill(32));
			addSkill(new G_Skill(33));
			addSkill(new G_Skill(34));
			addSkill(new G_Skill(53));
			addSkill(new G_Skill(54));
			addSkill(new G_Skill(55));
			addSkill(new G_Skill(56));
			addSkill(new G_Skill(57));
			addSkill(new G_Skill(58));
			addSkill(new G_Skill(67));
			addSkill(new G_Skill(68));
			addSkill(new G_Skill(69));
			addSkill(new G_Skill(70));
			addSkill(new G_Skill(73));
			addSkill(new G_Skill(78));
			break;
		}
		case 3: {
			addSkill(new G_Skill(20));
			addSkill(new G_Skill(21));
			addSkill(new G_Skill(29));
			addSkill(new G_Skill(30));
			addSkill(new G_Skill(35));
			addSkill(new G_Skill(36));
			addSkill(new G_Skill(42));
			addSkill(new G_Skill(43));
			addSkill(new G_Skill(44));
			addSkill(new G_Skill(45));
			addSkill(new G_Skill(46));
			addSkill(new G_Skill(65));
			addSkill(new G_Skill(66));
			addSkill(new G_Skill(74));
			addSkill(new G_Skill(77));
			break;
		}
		case 4: {
			addSkill(new G_Skill(79));
			addSkill(new G_Skill(80));
			addSkill(new G_Skill(81));
			addSkill(new G_Skill(82));
			addSkill(new G_Skill(83));
			addSkill(new G_Skill(84));
			addSkill(new G_Skill(85));
			addSkill(new G_Skill(86));
			addSkill(new G_Skill(87));
			addSkill(new G_Skill(88));
			addSkill(new G_Skill(89));
			addSkill(new G_Skill(90));
			addSkill(new G_Skill(91));
			addSkill(new G_Skill(92));
			addSkill(new G_Skill(93));
			addSkill(new G_Skill(94));
			addSkill(new G_Skill(95));
			addSkill(new G_Skill(96));
			addSkill(new G_Skill(97));
			addSkill(new G_Skill(98));
			break;
		}
		default:
			break;
		}
	}

	public void removeSkill(G_Skill skill) {
		while (skillList.contains(skill)) {
			skillList.remove(skill);
		}
	}

	public void setSkill(G_Player player, G_Skill skill) {

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
