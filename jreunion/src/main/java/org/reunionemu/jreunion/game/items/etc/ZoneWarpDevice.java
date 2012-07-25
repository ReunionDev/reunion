package org.reunionemu.jreunion.game.items.etc;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.server.Map;
import org.reunionemu.jreunion.server.World;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
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
	public void use(Item<?> item, LivingObject user, int slot) {
		/* item.gemNumber 	it's teleport Map ID
		 * item.extrastatus it's teleport unknown value
		 */
		if(user instanceof Player){
			Player player = (Player)user;
			World world = player.getClient().getWorld();
			Map map = world.getMap(item.getGemNumber());
			world.getCommand().GoToWorld((Player)user, map, item.getExtraStats());
		}
		
	}
}