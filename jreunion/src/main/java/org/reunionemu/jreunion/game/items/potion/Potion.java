package org.reunionemu.jreunion.game.items.potion;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.etc.Etc;
import org.reunionemu.jreunion.server.REHandler;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class Potion extends Etc implements Usable {
	
	private static int ticks = 4;
	private static int tickLength = 500;
	

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
	public boolean use(final Item<?> item, final LivingObject user, int quickSlotPosition, int unknown) {
	
		if(user instanceof Player){
			Player player = (Player)user;
			item.startJob(createJob(player, item), Potion.tickLength);

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

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);
		
		this.setEffect(Integer.parseInt(item.getMemberValue("Effect")));		
	}
	
	public abstract void effect(Player target, int effect);
	
	public Runnable createJob(final Player player, final Item<?> item){
		return  new REHandler(new Runnable() {
			int left = getEffect();
			int ticks = Potion.ticks;

			@Override
			public void run() {
				if(ticks > 0) {
					int effect = getEffect() / Potion.ticks;
					effect(player, Math.min(left, effect));
					left -= effect;
					ticks--;
				} else {
					item.stopJob();
				}
			}
		});
	}
}