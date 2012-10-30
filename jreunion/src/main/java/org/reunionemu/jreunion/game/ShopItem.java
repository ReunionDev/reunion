package org.reunionemu.jreunion.game;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class ShopItem {

	private ShopPosition shopPosition;

	private int goldBars;

	private int silverBars;

	private int bronzeBars;

	private Item<?> item;

	private long price;

	public ShopItem(ShopPosition shopPosition, int goldBars, int silverBars, int bronzeBars, long price, Item<?> item) {
		setShopPosition(shopPosition);
		setGoldBars(goldBars);
		setSilverBars(silverBars);
		setBronzeBars(bronzeBars);
		setPrice(price);
		setItem(item);
	}

	public Item<?> getItem() {
		return item;
	}

	public ShopPosition getShopPosition() {
		return shopPosition;
	}

	public void setItem(Item<?> item) {
		this.item = item;
	}

	public void setShopPosition(ShopPosition shopPosition) {
		this.shopPosition = shopPosition;
	}

	public int getGoldBars() {
		return goldBars;
	}

	public void setGoldBars(int goldBars) {
		this.goldBars = goldBars;
	}

	public int getSilverBars() {
		return silverBars;
	}

	public void setSilverBars(int silverBars) {
		this.silverBars = silverBars;
	}

	public int getBronzeBars() {
		return bronzeBars;
	}

	public void setBronzeBars(int bronzeBars) {
		this.bronzeBars = bronzeBars;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}
}