package org.reunionemu.jreunion.game.items.etc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.equipment.ChakuranWeapon;
import org.reunionemu.jreunion.server.Map;
import org.reunionemu.jreunion.server.World;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ZoneWarpDevice extends Etc implements Usable{
	
	public ZoneWarpDevice(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	@Override
	public boolean use(Item<?> item, LivingObject user, int quickSlotPosition, int unknown) {
		/* item.gemNumber 	it's teleport Map ID
		 * item.extrastatus it's teleport unknown value
		 */
		
		if(user instanceof Player){
			Player player = (Player)user;
			World world = player.getClient().getWorld();
			Map map = world.getMap((int)item.getGemNumber());
			
			world.getCommand().GoToWorld((Player) user, map,
					(int)item.getExtraStats());

			if (player.getClient().getVersion() >= 2000) {
				player.getClient().sendPacket(Type.UQ_ITEM, 1,
						quickSlotPosition, item.getEntityId(), unknown);
			}
			
			return true;
		} else {
			LoggerFactory.getLogger(this.getClass()).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
}