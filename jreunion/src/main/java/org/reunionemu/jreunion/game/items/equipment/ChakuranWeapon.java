package org.reunionemu.jreunion.game.items.equipment;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.SpecialWeapon;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class ChakuranWeapon extends SpecialWeapon implements Usable{
	
	private float memoryDmg;
	
	private float demolitionDmg;
	
	private int minSkillLevel;
	
	public ChakuranWeapon(int id) {
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
	public void use(Item<?> chakuranWeapon, LivingObject user, int slot) {
		
		Player player = null;
		
		if(user instanceof Player)
			player = (Player) user;
		
		chakuranWeapon.setExtraStats(chakuranWeapon.getExtraStats() - 20);
		player.setStamina(player.getStamina() - getStmUsed());
		DatabaseUtils.getDinamicInstance().saveItem(chakuranWeapon);
	}
}