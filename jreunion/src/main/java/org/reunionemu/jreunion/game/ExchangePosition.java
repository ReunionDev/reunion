package org.reunionemu.jreunion.game;

public class ExchangePosition extends InventoryPosition {

	
	public ExchangePosition(int posX, int posY) {
		super(posX, posY, EXCHANGE_TAB);
	}
	
	@Override
	protected InventoryPosition instanciate(int posX, int posY, int tab) {
		return new ExchangePosition(posX, posY);
	}
}