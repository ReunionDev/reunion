package org.reunionemu.jreunion.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Server;
import org.slf4j.LoggerFactory;

public abstract class Item<T extends ItemType> implements Entity{
	
	private int entityId = -1;
			
	private int unknown1;
	
	private int unknown2;
	
	private int unknown3;
	
	private ItemPosition position;
	
	static private ScheduledExecutorService jobService = Executors.newScheduledThreadPool(1);
	
	private ScheduledFuture<?> job;
	
	public ItemPosition getPosition() {
		return position;
	}

	public void setPosition(ItemPosition position) {
		this.position = position;
	}

	public Item() {
		
	}
	
	public abstract T getType();
	
	public int getEntityId() {
		return entityId;
	}
	
	public abstract Long getItemId();
	
	public abstract void setItemId(long itemId);

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public abstract long getExtraStats();

	public abstract long getGemNumber();

	
	public void setExtraStats(long extraStats) {
		if(getType()!=null){
			getType().setExtraStats(this);
		}
	}
	
	public boolean is(Class<?> itemType){
		return itemType.isAssignableFrom(getType().getClass());
	}

	public void setGemNumber(long gemNumber) {
		if(getType()!=null){
			getType().setGemNumber(this);
		}
	}
	
	public boolean use(LivingObject livingObject, int quickSlotBarPosition, int unknown){
		
		if(is(Usable.class)){
			return ((Usable)getType()).use(this, livingObject, quickSlotBarPosition, unknown);
		}else{
			LoggerFactory.getLogger(Item.class).error("Item "+this+" is not usable");
			return false;
			//throw new IllegalArgumentException(getType()+" is not Usable");
		}
	}
	
	public int getGradeLevel(){
		
		int gemNumber = (int)getGemNumber();
		
		if(((PlayerItem)getType()).getLevel() < 181) {
			return (gemNumber/1>0?1:0)+(gemNumber/3>0?1:0)+(gemNumber/6>0?1:0)+(gemNumber/10>0?1:0)+(gemNumber/15>0?1:0);
		}
		else{
			return gemNumber;
		}
	}
	
	public void upgrade(Player player, Slot slot)
	{	
		if(!this.getType().isUpgradable()){
			player.getClient().sendPacket(Type.SAY, getType().getName()+" it's not upgradable.");
			return;
		}
		boolean upgrade = false;
		
		if(this.getType() instanceof PlayerItem){
			PlayerItem pi = (PlayerItem) this.getType();
			int uppamount = 0;
			
			int actualGemNumber = (int)getGemNumber();
			
			if(pi.getLevel() < 181 && actualGemNumber < 15)
			{
				/* und Ruessis
				 * +1   +12%             100%
				 * +2   +26%              80%
				 * +3   +44%              60%
				 * +4   +68%              40%
				 * +5  +100%              20% (chance to return to +0)
				 */
				
				//int actualLevel = getGradeLevel();
				
				if (actualGemNumber == 0 || actualGemNumber == 2
						|| actualGemNumber == 5 || actualGemNumber == 9
						|| actualGemNumber == 14)
				{
					float r = Server.getRand().nextFloat();
					
					upgrade = ((actualGemNumber == 0) ? true
							: ((actualGemNumber == 2 && r <= .80) ? true
									: ((actualGemNumber == 5 && r <= .60) ? true
											: ((actualGemNumber == 9 && r <= .40) ? true
													: ((actualGemNumber == 14 && r <= .20) ? true
															: false)))));
					
					if (!upgrade)
					{
						uppamount = ((actualGemNumber == 2) ? -1
								: ((actualGemNumber == 5) ? -2
										: ((actualGemNumber == 9) ? -3
												: ((actualGemNumber == 14) ? -4
														: ((actualGemNumber == 14 && Server
																.getRand()
																.nextFloat() <= .05) ? -14
																: 0)))));
						player.getClient().sendPacket(Type.SAY, "Upgrading of item "+ pi.getName() +" failed");
						
					}
					else
					{
						uppamount = 1;
						player.getClient().sendPacket(Type.SAY, "Upgrading of item "+ pi.getName() +" was successfull");
					}
				}
				else
				{
					upgrade = true;
					uppamount = 1;
				}
			}
			else
			{
				player.getClient().sendPacket(Type.SAY, "Upgrading of items with bigger level than 180 is not implemented yet");
			}

			setGemNumber(getGemNumber()+uppamount);
			DatabaseUtils.getDinamicInstance().saveItem(this);
			DatabaseUtils.getDinamicInstance().deleteItem(player.getInventory().getHoldingItem().getItem().getItemId());
			player.getInventory().setHoldingItem(null);
			player.setDefense();
			
			player.getClient().sendPacket(Type.UPGRADE, this, slot,((upgrade) ? 1 : 0));
		}
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

	public abstract Integer getDurability();

	public abstract void setDurability(Integer durability);
	
	public void startJob(Runnable runnable, long period){
		job = jobService.scheduleAtFixedRate(runnable, 0l, period, TimeUnit.MILLISECONDS);
	}
	
	public void stopJob(){
		job.cancel(false);
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
