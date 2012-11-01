package org.reunionemu.jreunion.game;

public class HandPosition extends InventoryPosition {

	public final static HandPosition INSTANCE = new HandPosition();
	
	private HandPosition() {
		super(HAND_X, HAND_Y, HAND_TAB);
	}
	
	@Override
	protected InventoryPosition instanciate(int posX, int posY, int tab) {
		return INSTANCE;
	}
}