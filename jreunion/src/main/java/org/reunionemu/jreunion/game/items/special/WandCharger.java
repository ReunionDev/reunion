package org.reunionemu.jreunion.game.items.special;

import org.apache.log4j.Logger;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.game.items.equipment.ChakuranWeapon;
import org.reunionemu.jreunion.game.items.equipment.WandWeapon;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;



/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class WandCharger extends ScrollAndSpellBook implements Usable{
	
	public WandCharger(int id) {
		super(id);
		loadFromReference(id);
	}
	
	@Override
	public void use(Item<?> wandCharger, LivingObject user, int quickSlotPosition, int unknown){
		if (user instanceof Player) {
			Player player = ((Player) user);
			Item<?> wandWeapon = player.getEquipment().getItem(Slot.OFFHAND);
			if(wandWeapon.getType() instanceof WandWeapon){
				if(((WandWeapon)wandWeapon.getType()).getSkillLevel() != this.getSkillLevel()){
					Logger.getLogger(WandCharger.class).warn("POSSIBLE CHEAT DETECTED: used Wand Charger level "+this.getSkillLevel()+
							" with wand weapon level "+((WandWeapon)wandWeapon.getType()).getSkillLevel());
					return;
				}
			}
			wandWeapon.setGemNumber(wandWeapon.getType().getMaxGemNumber());
			DatabaseUtils.getDinamicInstance().saveItem(wandWeapon);
			if(player.getClient().getVersion() < 2000)
				player.getClient().sendPacket(Type.USQ,quickSlotPosition,Slot.OFFHAND.value(),wandWeapon.getGemNumber(),wandWeapon.getExtraStats());
			else
				player.getClient().sendPacket(Type.UQ_ITEM, 1, quickSlotPosition, wandCharger.getEntityId(),
						wandCharger.getGemNumber(), wandCharger.getExtraStats(), 5);
		}
		else
			Logger.getLogger(WandCharger.class).warn(this.getName() + " not implemented for " + user.getName());
	}
}