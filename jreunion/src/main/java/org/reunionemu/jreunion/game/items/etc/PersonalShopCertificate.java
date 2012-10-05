package org.reunionemu.jreunion.game.items.etc;

import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.PersonalShop;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PersonalShopCertificate extends Etc implements Usable{
	
	public PersonalShopCertificate(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
	
	@Override
	public boolean use(Item<?> item, LivingObject user, int quickSlotPosition, int unknown) {
				
		if(user instanceof Player){
			Player player = (Player)user;
			
			player.setShop(new PersonalShop(player, getTabsAmmount()));
			player.getPosition().getLocalMap().addShop(player.getShop());
			
			player.getClient().sendPacket(Type.U_SHOP, "open", null, getTabsAmmount()-1);
			player.getClient().sendPacket(Type.USQ, "succ", quickSlotPosition, -1, item);
			return true;
		} else {
			LoggerFactory.getLogger(this.getClass()).warn(this.getName() + " not implemented for " + user.getName());
		}
		
		return false;
	}
	
	public int getTabsAmmount(){
		switch(getTypeId()){
			case 1348: return 1;
			case 1349: return 2;
			case 1350: return 3;
			default: return 1;
		}
	}
}