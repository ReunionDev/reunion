package org.reunionemu.jreunion.game.items.etc;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;



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
	public void use(Item<?> grindStone, LivingObject user, int quickSlotPosition){
		if (user instanceof Player) {
			Player player = ((Player) user);
			Item<?> slayerWeapon = player.getEquipment().getItem(Slot.SHOULDER);
			slayerWeapon.setExtraStats(slayerWeapon.getExtraStats() + this.getMaxExtraStats());
			DatabaseUtils.getDinamicInstance().saveItem(slayerWeapon);
			player.getClient().sendPacket(Type.USQ,
											quickSlotPosition,
											Slot.SHOULDER.value(),
											slayerWeapon.getGemNumber(),
											slayerWeapon.getExtraStats());
		}
			
	}
}