package org.reunionemu.jreunion.game;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class HumanPlayer extends Player {

	public HumanPlayer(Client client) {
		super(client);
	}

	public long getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 30) +(getLeadership() / 2);
	}
	
	@Override
	public long getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 30)+ (getLeadership() / 2);		
	}
	
	public long getMaxMana(){
		return Tools.statCalc(getWisdom(), 50) + (getLeadership() / 2);		
	}
	public long getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}

	@Override
	public long getBaseDamage() {
		return (getLevel() / 6) + (getDexterity() / 2)+ (getStrength()/3);
	}
	
	@Override
	public List<Skill> getDefensiveSkills(){
		List<Skill> skillList = new Vector<Skill>();
	
		skillList.add(getSkill(30));
		
		return skillList;
	}
	
}