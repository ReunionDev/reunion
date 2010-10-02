package com.googlecode.reunion.jreunion.game.items.potion;

import java.util.Timer;
import java.util.TimerTask;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jreunion.game.Etc;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.server.Reference;

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
	public void use(final LivingObject user) {
		final Timer timer = new Timer();
		final long start = System.currentTimeMillis();
		
		TimerTask o= new TimerTask(){
			int left = getEffect();
			int ticks = Potion.ticks;
			@Override
			public void run() {
				System.out.println(System.currentTimeMillis()-start);
				int effect = getEffect()/Potion.ticks;
				
				effect(user, Math.min(left, effect));
				
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
		
		
		ParsedItem item = Reference.getInstance().getItemReference()
		.getItemById(id);
		
		this.setEffect(Integer.parseInt(item.getMemberValue("Effect")));		
	}
	
	public abstract void effect(LivingObject target, int effect);
	
}