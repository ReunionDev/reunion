package com.googlecode.reunion.jreunion.game.items.equipment;

import java.util.List;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.game.items.SpecialWeapon;
import com.googlecode.reunion.jreunion.game.skills.bulkan.SecondAttack;
import com.googlecode.reunion.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SlayerWeapon extends SpecialWeapon implements Usable {
	
	private float memoryDmg;
	
	private float demolitionDmg;
	
	private int minSkillLevel;

	public SlayerWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	public float getMemoryDmg() {
		return memoryDmg;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setMemoryDmg(0);
			setDemolitionDmg(0);
			setMinSkillLevel(0);
		} else {
			if (item.checkMembers(new String[] { "MemoryDmg" })) {
				// use member from file
				setMemoryDmg(Float.parseFloat(item.getMemberValue("MemoryDmg")));
			} else {
				// use default
				setMemoryDmg(0);
			}
			if (item.checkMembers(new String[] { "Demolition" })) {
				// use member from file
				setDemolitionDmg(Float.parseFloat(item.getMemberValue("Demolition")));
			} else {
				// use default
				setDemolitionDmg(0);
			}
			if (item.checkMembers(new String[] { "Skillevel" })) {
				// use member from file
				setMinSkillLevel(Integer.parseInt(item.getMemberValue("Skillevel")));
			} else {
				// use default
				setMinSkillLevel(0);
			}
		}
	}

	public void setMemoryDmg(float memoryDmg) {
		this.memoryDmg = memoryDmg;
	}

	public float getDemolitionDmg() {
		return demolitionDmg;
	}

	public void setDemolitionDmg(float demolitionDmg) {
		this.demolitionDmg = demolitionDmg;
	}

	public int getMinSkillLevel() {
		return minSkillLevel;
	}

	public void setMinSkillLevel(int minSkillLevel) {
		this.minSkillLevel = minSkillLevel;
	}

	@Override
	public void use(Item<?> item, LivingObject user) {
		
		Player player = null;
		
		if(user instanceof Player)
			player = (Player) user;
		
		item.setExtraStats(item.getExtraStats() - 20);
		player.setStamina(player.getStamina() - getStmUsed());
	}
}