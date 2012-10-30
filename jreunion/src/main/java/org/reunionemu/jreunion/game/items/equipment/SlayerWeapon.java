package org.reunionemu.jreunion.game.items.equipment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.SpecialWeapon;
import org.reunionemu.jreunion.server.Database;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Server;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
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
	
	public float getDemolition() {
		return Server.getRand().nextFloat() <= this.getDemolitionDmg() ? 
				Server.getInstance().getWorld().getServerSettings().getDemolitionModifier() : 1;
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
	public boolean use(Item<?> slayerWeapon, LivingObject user, int quickSlotPosition, int unknown) {
				
		if(user instanceof Player) {
			Player player = (Player) user;
		
			if (slayerWeapon.getExtraStats() <= 0) {
				LoggerFactory.getLogger(this.getClass()).warn(
						"Possible cheat detected: player " + player
								+ " is trying to use empty " + this.getName() + ".");
				return false;
			}

			//update Slayer uses remain
			int usesRemain = (int)slayerWeapon.getExtraStats() - 20;
			if(usesRemain < 0){
				return false;
			}
			slayerWeapon.setExtraStats(usesRemain);
			slayerWeapon.save();
			
			//update player stamina
			long staminaRemain = player.getStamina() - getStmUsed();
			if(staminaRemain < 0){
				return false;
			}
			player.setStamina(staminaRemain);
			
			if (player.getClient().getVersion() >= 2000)
				player.getClient().sendPacket(Type.UQ_ITEM, 1, quickSlotPosition,
						slayerWeapon.getEntityId(), unknown);
			return true;
		} else {
			LoggerFactory.getLogger(SlayerWeapon.class).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
}