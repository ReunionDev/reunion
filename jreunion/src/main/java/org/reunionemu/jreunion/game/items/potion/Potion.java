package org.reunionemu.jreunion.game.items.potion;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.equipment.ChakuranWeapon;
import org.reunionemu.jreunion.game.items.etc.Etc;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Potion extends Etc implements Usable {
	
	public static int ticks = 4;
	public static int tickLength = 500;

	public Potion(int id) {
		super(id);
	}
	
	int effect;

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}
	
	@Override
	public void use(Item<?> item, final LivingObject user, int quickSlotPosition, int unknown) {
		final Timer timer = new Timer();
		
		if(user instanceof Player){
			TimerTask o = new TimerTask() {
				int left = getEffect();
				int ticks = Potion.ticks;
				Player player = (Player) user;

				@Override
				public void run() {
					int effect = getEffect() / Potion.ticks;
					effect(player, Math.min(left, effect));
					left -= effect;
					ticks--;
					if (ticks == 0)
						timer.cancel();
				}
			};
			timer.schedule(o, 0, Potion.tickLength);

			if (((Player) user).getClient().getVersion() >= 2000)
				((Player) user).getClient().sendPacket(Type.UQ_ITEM, 1,
						quickSlotPosition, item.getEntityId(), unknown);
		}
		else
			Logger.getLogger(Potion.class).warn(this.getName() + " not implemented for " + user.getName());
	
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);
		
		this.setEffect(Integer.parseInt(item.getMemberValue("Effect")));		
	}
	
	public abstract void effect(Player target, int effect);
	
}