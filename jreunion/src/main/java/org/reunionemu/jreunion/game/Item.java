package org.reunionemu.jreunion.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class Item<T extends ItemType> implements Entity{
	
	private T type;
	
	private int entityId = -1;
	
	private int itemId = -1; //for database;

	private int gemNumber;

	private int extraStats;
	
	private int durability;
	
	private int unknown1;
	
	private int unknown2;
	
	private int unknown3;
	
	private ItemPosition position;
	
	private ScheduledExecutorService jobService = Executors.newScheduledThreadPool(1);
	
	public ItemPosition getPosition() {
		return position;
	}

	public void setPosition(ItemPosition position) {
		this.position = position;
	}

	public Item(T itemType) {
		
		setType(itemType); 
	}
	
	public T getType() {
		return type;
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public int getExtraStats() {
		return extraStats;
	}

	public int getGemNumber() {
		return gemNumber;
	}

	private void setType(T type) {
		this.type = type;
	}
	
	public void setExtraStats(int extraStats) {
		this.extraStats = extraStats;
		getType().setExtraStats(this);
	}
	
	public boolean is(Class<?> itemType){
		return itemType.isAssignableFrom(getType().getClass());
	}

	public void setGemNumber(int gemNumber) {
		this.gemNumber = gemNumber;
		getType().setGemNumber(this);
	}
	
	public void use(LivingObject livingObject, int quickSlotBarPosition, int unknown){
		
		if(is(Usable.class)){
			((Usable)getType()).use(this, livingObject, quickSlotBarPosition, unknown);
		}else{
			Logger.getLogger(Item.class).error("Item "+this+" is not usable");
			//throw new IllegalArgumentException(getType()+" is not Usable");
		}
	}
	
	public int getGradeLevel(){
		
		int gemNumber = getGemNumber();
		
		if(((PlayerItem)getType()).getLevel() < 181) {
			return (gemNumber/1>0?1:0)+(gemNumber/3>0?1:0)+(gemNumber/6>0?1:0)+(gemNumber/10>0?1:0)+(gemNumber/15>0?1:0);
		}
		else{
			return gemNumber;
		}
	}
	
	public void upgrade(Player player, Slot slot)
	{	
		setGemNumber(getGemNumber()+1);
		DatabaseUtils.getDinamicInstance().saveItem(this);
		DatabaseUtils.getDinamicInstance().deleteItem(player.getInventory().getHoldingItem().getItem().getItemId());
		player.getInventory().setHoldingItem(null);
		player.setDefense();
		
		player.getClient().sendPacket(Type.UPGRADE, this, slot,1);
	}
	
	public void update(Player player, Slot slot){
		Item<?> holdingItem = player.getInventory().getHoldingItem().getItem();
		
		if(holdingItem != null){
			setExtraStats(getExtraStats() + holdingItem.getType().getMaxExtraStats());
			DatabaseUtils.getDinamicInstance().deleteItem(holdingItem.getItemId());
			player.getInventory().setHoldingItem(null);
			player.save();
			DatabaseUtils.getDinamicInstance().saveItem(this);
			player.getClient().sendPacket(Type.UPDATE_ITEM, this, 1);
		}
	}
	
	public static Item<?> load(int itemId){
			
		return DatabaseUtils.getDinamicInstance().loadItem(itemId);
			
	}

	public int getUnknown1() {
		return unknown1;
	}

	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public int getUnknown2() {
		return unknown2;
	}

	public void setUnknown2(int unknown2) {
		this.unknown2 = unknown2;
	}
	
	public int getUnknown3() {
		return unknown3;
	}

	public void setUnknown3(int unknown3) {
		this.unknown3 = unknown3;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}
	
	public void startJob(Runnable job, long period){
		jobService.scheduleAtFixedRate(job, 0l, period, TimeUnit.MILLISECONDS);
	}
	
	public void stopJob(){
		jobService.shutdown();
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		buffer.append("id:");
		buffer.append(getEntityId());
		buffer.append("("+getItemId()+")");
		buffer.append(", ");
		
		buffer.append("name:");
		buffer.append(getType().getName());	
				
		buffer.append("}");
		return buffer.toString();
	}
	
}
