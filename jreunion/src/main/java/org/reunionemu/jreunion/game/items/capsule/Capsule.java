package org.reunionemu.jreunion.game.items.capsule;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.etc.Etc;
import org.reunionemu.jreunion.server.Reference;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public abstract class Capsule extends Etc implements Usable {
	
	public static int ticks = 4;
	public static int tickLength = 500;

	public Capsule(int id) {
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
	public boolean use(Item<?> item, final LivingObject user, int quickSlotPosition, int unknown) {
		//TODO: implement capsule effect	
		return false;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		ParsedItem item = Reference.getInstance().getItemReference().getItemById(id);
		
		this.setEffect(Integer.parseInt(item.getMemberValue("Effect")));
	}
	
	public abstract void effect(Player target, int effect);
	
}