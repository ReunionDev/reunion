package org.reunionemu.jreunion.game.items.equipment;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.SummonWeapon;
import org.reunionemu.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class RingWeapon extends SummonWeapon {
	public RingWeapon(int id) {
		super(id);
		loadFromReference(id);
	}

	
	@Override
	public boolean use(Player player) {
		int manaUsed = getManaUsed();
		if (manaUsed > 0) {
			synchronized(player){
				if(player.getMana() < manaUsed) {					
					return false;				
				}
				player.setMana(player.getMana()- manaUsed);				
			}
		}
		return true;
	}
	
	private int manaUsed;

	public void setManaUsed(int manaUsed) {
		this.manaUsed = manaUsed;
	}

	
	
	public int getManaUsed() {
		return manaUsed;
	}
	
	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:	
			setManaUsed(0);
			
		} else {
		
			if (item.checkMembers(new String[] { "ManaUsed" })) {
				// use member from file
				setManaUsed(Integer.parseInt(item.getMemberValue("ManaUsed")));
			} else {
				// use default
				setManaUsed(0);
			}
		}
	}

}