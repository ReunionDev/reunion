package org.reunionemu.jreunion.game.items.etc;

import org.reunionemu.jreunion.dao.ItemDao;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.server.Database;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;



/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class GrindStone extends WhetStone implements Usable{
		
	public GrindStone(int id) {
		super(id);
		loadFromReference(id);
	}
	
	@Override
	public boolean use(Item<?> grindStone, LivingObject user, int quickSlotPosition, int unknown){
		if (user instanceof Player) {
			Player player = ((Player) user);
			Item<?> slayerWeapon = player.getEquipment().getItem(Slot.SHOULDER);
			
			//update Slayer uses remain
			int usesRemain = (int)(slayerWeapon.getExtraStats() + this.getMaxExtraStats());
			if(usesRemain > slayerWeapon.getType().getMaxExtraStats()){
				usesRemain = (int)slayerWeapon.getType().getMaxExtraStats();
			}
			slayerWeapon.setExtraStats(usesRemain);
			slayerWeapon.save();
			
			if(player.getClient().getVersion() < 2000){
				player.getClient().sendPacket(Type.USQ, "remain", quickSlotPosition, Slot.SHOULDER.value(), slayerWeapon);
			} else { 
				player.getClient().sendPacket(Type.UQ_ITEM, 1, quickSlotPosition, grindStone.getEntityId(), unknown, slayerWeapon.getExtraStats(), 3);
			}
			
			return true;
		} else {
			LoggerFactory.getLogger(GrindStone.class).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
}