package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Skill;

public class SkillManager {

	java.util.Map<Integer,Skill> skills = new HashMap<Integer,Skill>();

	java.util.Map<Race,List<Skill>> defaultSkills = new HashMap<Race,List<Skill>>();
	
	public SkillManager(){
		

		Parser parser = new Parser();
		try {
			parser.Parse("data/Skills.dta");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Iterator<ParsedItem> iter = parser.getItemListIterator();
		
		while(iter.hasNext()){
			
			ParsedItem item = iter.next();
			if(!item.checkMembers(new String[]{"Id","Class"}))
				continue;
			
			int id = Integer.parseInt(item.getMemberValue("Id"));
			String className = "com.googlecode.reunion.jreunion.game.skills."+item.getMemberValue("Class");
			
			
			Skill skill = (Skill) ClassFactory.create(className, this, id);
			if(skill==null)
				continue;
				
			skills.put(id, skill);

		
		}
		parser.clear();
		
		for(Race race : Race.values()){
			
			List<Skill> raceSkillList = new Vector<Skill>();
			raceSkillList.add(skills.get(0));
			switch(race) {
			case BULKAN: {
				
				raceSkillList.add(skills.get(1));
				raceSkillList.add(skills.get(1));
				raceSkillList.add(skills.get(2));
				raceSkillList.add(skills.get(17));
				raceSkillList.add(skills.get(18));
				raceSkillList.add(skills.get(19));
				raceSkillList.add(skills.get(31));
				raceSkillList.add(skills.get(37));
				raceSkillList.add(skills.get(38));
				raceSkillList.add(skills.get(39));
				raceSkillList.add(skills.get(40));
				raceSkillList.add(skills.get(41));
				raceSkillList.add(skills.get(60));
				raceSkillList.add(skills.get(61));
				raceSkillList.add(skills.get(71));
				raceSkillList.add(skills.get(75));
				break;
			}
			case KAILIPTON: {
				raceSkillList.add(skills.get(3));
				raceSkillList.add(skills.get(4));
				raceSkillList.add(skills.get(5));
				raceSkillList.add(skills.get(7));
				raceSkillList.add(skills.get(8));
				raceSkillList.add(skills.get(10));
				raceSkillList.add(skills.get(11));
				raceSkillList.add(skills.get(12));
				raceSkillList.add(skills.get(13));
				raceSkillList.add(skills.get(14));
				raceSkillList.add(skills.get(15));
				raceSkillList.add(skills.get(26));
				raceSkillList.add(skills.get(27));
				raceSkillList.add(skills.get(28));
				raceSkillList.add(skills.get(47));
				raceSkillList.add(skills.get(48));
				raceSkillList.add(skills.get(49));
				raceSkillList.add(skills.get(50));
				raceSkillList.add(skills.get(51));
				raceSkillList.add(skills.get(52));
				raceSkillList.add(skills.get(62));
				raceSkillList.add(skills.get(63));
				raceSkillList.add(skills.get(64));
				raceSkillList.add(skills.get(72));
				raceSkillList.add(skills.get(76));
				break;
			}
			case AIDIA: {
				raceSkillList.add(skills.get(6));
				raceSkillList.add(skills.get(22));
				raceSkillList.add(skills.get(24));
				raceSkillList.add(skills.get(32));
				raceSkillList.add(skills.get(33));
				raceSkillList.add(skills.get(34));
				raceSkillList.add(skills.get(53));
				raceSkillList.add(skills.get(54));
				raceSkillList.add(skills.get(55));
				raceSkillList.add(skills.get(56));
				raceSkillList.add(skills.get(57));
				raceSkillList.add(skills.get(58));
				raceSkillList.add(skills.get(67));
				raceSkillList.add(skills.get(68));
				raceSkillList.add(skills.get(69));
				raceSkillList.add(skills.get(70));
				raceSkillList.add(skills.get(73));
				raceSkillList.add(skills.get(78));
				break;
			}
			case HUMAN: {
				raceSkillList.add(skills.get(20));
				raceSkillList.add(skills.get(21));
				raceSkillList.add(skills.get(29));
				raceSkillList.add(skills.get(30));
				raceSkillList.add(skills.get(35));
				raceSkillList.add(skills.get(36));
				raceSkillList.add(skills.get(42));
				raceSkillList.add(skills.get(43));
				raceSkillList.add(skills.get(44));
				raceSkillList.add(skills.get(45));
				raceSkillList.add(skills.get(46));
				raceSkillList.add(skills.get(65));
				raceSkillList.add(skills.get(66));
				raceSkillList.add(skills.get(74));
				raceSkillList.add(skills.get(77));
				break;
			}
			case HYBRIDER: {
				raceSkillList.add(skills.get(79));
				raceSkillList.add(skills.get(80));
				raceSkillList.add(skills.get(81));
				raceSkillList.add(skills.get(82));
				raceSkillList.add(skills.get(83));
				raceSkillList.add(skills.get(84));
				raceSkillList.add(skills.get(85));
				raceSkillList.add(skills.get(86));
				raceSkillList.add(skills.get(87));
				raceSkillList.add(skills.get(88));
				raceSkillList.add(skills.get(89));
				raceSkillList.add(skills.get(90));
				raceSkillList.add(skills.get(91));
				raceSkillList.add(skills.get(92));
				raceSkillList.add(skills.get(93));
				raceSkillList.add(skills.get(94));
				raceSkillList.add(skills.get(95));
				raceSkillList.add(skills.get(96));
				raceSkillList.add(skills.get(97));
				raceSkillList.add(skills.get(98));
				break;
			
			}
			default:
				continue;
			
			}
			defaultSkills.put(race, raceSkillList);
		}
	}
	public Skill getSkill(int id){
		
		return skills.get(id);
	}
	
	public void loadSkills(Player player){
		Race race = player.getRace();
		synchronized(player){
			player.getSkills().clear();
			for(Skill skill : defaultSkills.get(race)){
				player.getSkills().put(skill, 0);
	
			}
		}
		switch(race){
		case KAILIPTON:
			player.setSkillLevel(getSkill(3),1); //Fireball
			player.setSkillLevel(getSkill(4),1); //Lightning Ball
			player.setSkillLevel(getSkill(12),1); //Pebble Shot
			
		}
	}
}
