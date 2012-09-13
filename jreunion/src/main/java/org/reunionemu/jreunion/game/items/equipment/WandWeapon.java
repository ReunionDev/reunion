package org.reunionemu.jreunion.game.items.equipment;

import org.apache.log4j.Logger;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.SpecialWeapon;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Reference;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class WandWeapon extends SpecialWeapon implements Usable{
	
	private float accumulatedDmg;
	private int skillLevel;
	
	public WandWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);
		 
		// we getting informations on items
		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setAccumulatedDmg(0);
			setSkillLevel(0);

		} else {
			if (item.checkMembers(new String[] { "AccumulatedDmg" })) {
				// use member from file
				setAccumulatedDmg(Float.parseFloat(item.getMemberValue("AccumulatedDmg")));
			} else {
				// use default
				setAccumulatedDmg(0);
			}
			if (item.checkMembers(new String[] { "Skillevel" })) {
				// use member from file
				setSkillLevel(Integer.parseInt(item.getMemberValue("Skillevel")));
			} else {
				// use default
				setSkillLevel(0);
			}			 
		}
	}
	
	public float getAccumulatedDmg() {
		return accumulatedDmg;
	}

	public void setAccumulatedDmg(float accumulatedDmg) {
		this.accumulatedDmg = accumulatedDmg;
	}
	 
	public int getSkillLevel() {
		return skillLevel;
	}
 
	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}
	 
	@Override
	public boolean use(Item<?> wandWeapon, LivingObject user, int quickSlotPosition, int unknown) {
		
		if(user instanceof Player) {
			Player player = (Player) user;

			if (wandWeapon.getGemNumber() <= 0) {
				Logger.getLogger(WandWeapon.class).warn(
						"Possible cheat detected: player " + player
								+ " is trying to use empty " + this.getName()
								+ ".");
				return false;
			}

			//update WandWeapon uses remain
			int usesRemain = wandWeapon.getGemNumber() - 1;
			if(usesRemain < 0){
				return false;
			}
			wandWeapon.setGemNumber(usesRemain);
			DatabaseUtils.getDinamicInstance().saveItem(wandWeapon);
			
			//update player mana
			long manaRemain = player.getMana() - getManaUsed();
			if(manaRemain < 0){
				return false;
			}
			player.setMana(manaRemain);
			
			
			/*if (player.getClient().getVersion() >= 2000)
				player.getClient().sendPacket(Type.UQ_ITEM, 1,
						quickSlotPosition, wandWeapon.getEntityId(), unknown);
			*/
			return true;
		} else {
			Logger.getLogger(WandWeapon.class).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
	
}