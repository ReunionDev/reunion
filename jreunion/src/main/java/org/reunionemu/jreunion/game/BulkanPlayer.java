package org.reunionemu.jreunion.game;

import java.util.List;
import java.util.Vector;

import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Tools;



/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class BulkanPlayer extends Player {

	public BulkanPlayer(Client client) {
		super(client);
	}

	public long getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 50) +(getLeadership() / 2);
	}
	
	public long getMaxMana(){
		return Tools.statCalc((int)getWisdom(), 80) + (getLeadership() / 2);		
	}
	
	@Override
	public long getMaxHp(){
		return Tools.statCalc(getStrength(), 49) + Tools.statCalc((int)getConstitution(), 20)+ (getLeadership() / 2);		
	}
	
	public long getMaxStamina(){
		return Tools.statCalc(getStrength(), 60) + (getLeadership() / 2)+ getConstitution();
	}
	
	@Override
	public long getBaseDamage() {
		return getLevel()/6+(getStrength()/4)+(getConstitution()/5)+(getDexterity()/4);
	}
	
	@Override
	public List<Skill> getDefensiveSkills(){
		List<Skill> skillList = new Vector<Skill>();
		
		
		
		return skillList;
	}
}