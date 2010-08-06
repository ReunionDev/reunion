package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Exchange {

	private List<G_ExchangeItem> itemList;

	public G_Exchange() {
		itemList = new Vector<G_ExchangeItem>();
	}

	public void addItem(G_ExchangeItem item) {
		if (itemList.contains(item)) {
			return;
		}
		itemList.add(item);
	}

	public void clearExchange() {
		itemList.clear();
	}

	public G_ExchangeItem getItem(int posX, int posY) {
		Iterator<G_ExchangeItem> exchangeIter = itemListIterator();

		while (exchangeIter.hasNext()) {
			G_ExchangeItem exchangeItem = exchangeIter.next();

			for (int x = 0; x < exchangeItem.getItem().getSizeX(); x++) {
				for (int y = 0; y < exchangeItem.getItem().getSizeY(); y++) {
					if (posX == x + exchangeItem.getPosX()
							&& posY == y + exchangeItem.getPosY()) {
						return exchangeItem;
					}
				}
			}
		}
		return null;
	}

	public Iterator<G_ExchangeItem> itemListIterator() {
		return itemList.iterator();
	}

	public int listSize() {
		return itemList.size();
	}

	public void removeItem(G_ExchangeItem item) {
		if (!itemList.contains(item)) {
			return;
		}
		itemList.remove(item);
	}
}