package org.reunionemu.jreunion.game;

import java.util.*;

import org.reunionemu.jreunion.game.items.etc.ScrollOfNAgen;
import org.reunionemu.jreunion.server.PacketFactory.Type;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Exchange {

	private List<InventoryItem> itemList = new LinkedList<InventoryItem>();
	
	private Player owner;
	
	private Player otherTrader;
	
	private long money;
	
	private boolean isAccepted;

	public Exchange(Player player) {

		setOwner(player);
		setOtherTrader(null);
		setAccepted(false);
	}
	
	public List<InventoryItem> getList(){
		return itemList;
	}

	public void addItem(InventoryItem item) {
		if(!(item.getPosition() instanceof ExchangePosition)){
			throw new IllegalStateException("Adding non exchange item to exchange");			
		}
		if (itemList.contains(item)) {
			return;
		}
		if(getOtherTrader() != null){
			if(isAccepted() || getOtherTrader().getExchange().isAccepted()){
				disable();
			}
			getOtherTrader().getClient().sendPacket(Type.EXCH_INVEN_TO, item);
		}
		itemList.add(item);
	}

	public void clearExchange() {
		itemList.clear();
	}

	public InventoryItem getItem(int posX, int posY) {
		Iterator<InventoryItem> exchangeIter = itemListIterator();

		while (exchangeIter.hasNext()) {
			InventoryItem exchangeItem = exchangeIter.next();

			for (int x = 0; x < exchangeItem.getItem().getType().getSizeX(); x++) {
				for (int y = 0; y < exchangeItem.getItem().getType().getSizeY(); y++) {
					if (posX == x + exchangeItem.getPosition().getPosX()
							&& posY == y + exchangeItem.getPosition().getPosY()) {
						return exchangeItem;
					}
				}
			}
		}
		return null;
	}

	public Iterator<InventoryItem> itemListIterator() {
		return itemList.iterator();
	}
	
	public List<InventoryItem> getItemList(){
		return this.itemList;
	}

	public void setList(List<InventoryItem> itemList){
		this.itemList = itemList;
	}
	
	public int listSize() {
		return itemList.size();
	}

	public void removeItem(InventoryItem item) {
		if (!itemList.contains(item)) {
			return;
		}
		if(getOtherTrader() != null){
			if(isAccepted() || getOtherTrader().getExchange().isAccepted()){
				disable();
			}
			getOtherTrader().getClient().sendPacket(Type.EXCH_INVEN_FROM, item);
		}
		itemList.remove(item);
	}
	
	public boolean isItemsScrolls(){
		for(InventoryItem exchangeItem: itemList){
			Item<?> item = exchangeItem.getItem();
			if(!(item.is(ScrollOfNAgen.class))){
				return false;
			}
		}
		return true;
	}
	
	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public void request(Player target){
		if(target.isInCombat()){
			getOwner().getClient().sendPacket(Type.EXCH, "cancel");
			getOwner().getClient().sendPacket(Type.MSG, target.getName()+" user is in combat mode or unable to engage in an item exchange/refining trade.");
			return;
		}
		setOtherTrader(target);
		target.getExchange().setOtherTrader(getOwner());
		target.getClient().sendPacket(Type.EXCH_ASK, getOwner());
	}
	
	public void cancelRequest(){
		getOtherTrader().getClient().sendPacket(Type.EXCH, "cancel");
		getOwner().getClient().sendPacket(Type.EXCH, "cancel");
		getOtherTrader().getClient().sendPacket(Type.MSG, getOwner().getName()+" user has declined your request for an item exchange/refining trade.");
		setOtherTrader(null);
		getOwner().getExchange().setOtherTrader(null);
	}

	public void acceptRequest(){
		getOwner().getClient().sendPacket(Type.EXCH_START, getOtherTrader());
		for(InventoryItem exchangeItem : getOtherTrader().getExchange().getItemList()){
			getOwner().getClient().sendPacket(Type.EXCH_INVEN_TO, exchangeItem);
		}
		
		getOtherTrader().getClient().sendPacket(Type.EXCH_START, getOwner());
		for(InventoryItem exchangeItem : getOwner().getExchange().getItemList()){
			getOtherTrader().getClient().sendPacket(Type.EXCH_INVEN_TO, exchangeItem);
		}
	}
	
	public void disable(){
		setAccepted(false);
		getOtherTrader().getExchange().setAccepted(false);
		setMoney(0);
		getOtherTrader().getExchange().setMoney(0);
		getOwner().getClient().sendPacket(Type.EXCH, "disable");
		getOtherTrader().getClient().sendPacket(Type.EXCH, "disable");
	}
	
	public void tradeConfirmation(){
		if(getOtherTrader()==null)
			return;
		
		if(getOtherTrader().getExchange().isAccepted()){
			change();
			return;
		}
		
		setAccepted(true);
		getOtherTrader().getClient().sendPacket(Type.EXCH, "trade");
	}
	
	public void change(){
		if(getOtherTrader()==null)
			return;
		
		Player otherTrader = getOtherTrader();
		Exchange otherTraderExchange = otherTrader.getExchange();
		List<InventoryItem> otherTraderItemList = new Vector<InventoryItem>(otherTraderExchange.getItemList());
		long otherTraderMoney = otherTraderExchange.getMoney();
		long money = getMoney();
		
		//reset other trader exchange
		otherTraderExchange.setMoney(0);
		otherTraderExchange.setAccepted(false);
		otherTraderExchange.setOtherTrader(null);
		otherTraderExchange.setList(new Vector<InventoryItem>(getItemList()));
		
		//reset owner exchange
		setMoney(0);
		setAccepted(false);
		setOtherTrader(null);
		setList(otherTraderItemList);
		
		if(otherTraderMoney > 0){
			otherTrader.setLime(otherTrader.getLime() - otherTraderMoney);
			getOwner().setLime(getOwner().getLime() + otherTraderMoney);
		}
		
		if(money > 0){
			getOwner().setLime(getOwner().getLime() - money);
			otherTrader.setLime(otherTrader.getLime() + money);
		}
		
		getOwner().getClient().sendPacket(Type.EXCH, "change");
		otherTrader.getClient().sendPacket(Type.EXCH, "change");
	}
	
	public Player getOtherTrader() {
		return otherTrader;
	}

	public void setOtherTrader(Player otherTrader) {
		this.otherTrader = otherTrader;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {	
		getOtherTrader().getClient().sendPacket(Type.EXCH_MONEY, money);
		this.money = money;
	}

	public void addMoney(long money){
		if(getOtherTrader() == null)
			return;
		
		if(getOtherTrader().getExchange().isAccepted()){
			disable();
			return;
		}
		setMoney(money);
	}
	
	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}
}