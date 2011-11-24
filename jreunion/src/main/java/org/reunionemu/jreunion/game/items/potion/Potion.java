package org.reunionemu.jreunion.game.items.potion;

import java.util.Timer;
import java.util.TimerTask;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.etc.Etc;
import org.reunionemu.jreunion.server.Reference;

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
	public void use(Item<?> item, final LivingObject user) {
		final Timer timer = new Timer();
		
		TimerTask o= new TimerTask(){
			int left = getEffect();
			int ticks = Potion.ticks;
			Player player = (Player)user;
			
			@Override
			public void run() {
				int effect = getEffect()/Potion.ticks;
				effect(player, Math.min(left, effect));
				left-=effect;
				ticks--;
				if(ticks==0)
					timer.cancel();
			}
		};
		timer.schedule(o, 0, Potion.tickLength);		
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);
		
		this.setEffect(Integer.parseInt(item.getMemberValue("Effect")));		
	}
	
	public abstract void effect(Player target, int effect);
	
}