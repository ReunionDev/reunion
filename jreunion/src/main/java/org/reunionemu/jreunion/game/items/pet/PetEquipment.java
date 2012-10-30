package org.reunionemu.jreunion.game.items.pet;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.LocalMap;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class PetEquipment {
	
	private Item<?> body;

	private Item<?> foot;

	private Item<?> head;

	private Item<?> horn;

	private Item<?> tail;

	private Item<?> wing;

	//used when loading existing pet
	public PetEquipment(){
		
	}
	
	//used when creating a new pet
	public PetEquipment(Player player) {
		ItemManager itemManager = player.getClient().getWorld().getItemManager();
		LocalMap map = player.getPosition().getLocalMap();
		
		//official server default pet equipment
		Item<?> body = itemManager.create(578);
		Item<?> foot = itemManager.create(580);
		Item<?> head = itemManager.create(577);
		Item<?> tail = itemManager.create(581);
		Item<?> wing = itemManager.create(579);
		
		map.createEntityId(body);
		map.createEntityId(foot);
		map.createEntityId(head);
		map.createEntityId(tail);
		map.createEntityId(wing);
		
		this.setBody(body);
		this.setFoot(foot);
		this.setHead(head);
		this.setTail(tail);
		this.setWing(wing);
	}
	
	public int getTypeId(PetSlot slot){
		Item<?> item = getItem(slot); 
		return item == null ? -1 : item.getType().getTypeId();
	}
	
	public int getEntityId(PetSlot slot){
		Item<?> item = getItem(slot); 
		return item == null ? -1 : item.getEntityId();
	}

	public Item<?> getItem(PetSlot slot) {

		switch (slot) {
		case HORN:
			return getHorn();
		case HEAD:
			return getHead();
		case BODY:
			return getBody();
		case WING:
			return getWing();
		case FOOT:
			return getFoot();
		case TAIL:
			return getTail();
		}

		return null;
	}

	public boolean setItem(PetSlot slot, Item<?> item) {
		try{
			switch (slot) {
			case HORN: {
				setHorn(item);
				break;
			}
			case HEAD: {
				setHead(item);
				break;
			}
			case BODY: {
				setBody(item);
				break;
			}
			case WING: {
				setWing(item);
				break;
			}
			case FOOT: {
				setFoot(item);
				break;
			}
			case TAIL: {
				setTail(item);
				break;
			}
			}
		}catch(IllegalAccessError e){
			return false;
		}
		return true;

	}

	
	public Item<?> getBody() {
		return body;
	}

	public void setBody(Item<?> body) {
		
		if(body!=null&&!body.is(PetBody.class)){
			throw new IllegalArgumentException();
		}
		this.body = body;
	}
	public Item<?> getFoot() {
		return foot;
	}

	public void setFoot(Item<?> foot) {
		
		if(foot!=null&&!foot.is(PetFoot.class)){
			throw new IllegalArgumentException();
		}
		this.foot = foot;
	}
	public Item<?> getHead() {
		return head;
	}

	public void setHead(Item<?> head) {
		
		if(head!=null&&!head.is(PetHead.class)){
			throw new IllegalArgumentException();
		}
		this.head = head;
	}
	public Item<?> getHorn() {
		return horn;
	}

	public void setHorn(Item<?> horn) {
		
		if(horn!=null&&!horn.is(PetHorn.class)){
			throw new IllegalArgumentException();
		}
		this.horn = horn;
	}
	public Item<?> getTail() {
		return tail;
	}

	public void setTail(Item<?> tail) {
		
		if(tail!=null&&!tail.is(PetTail.class)){
			throw new IllegalArgumentException();
		}
		this.tail = tail;
	}
	public Item<?> getWing() {
		return wing;
	}

	public void setWing(Item<?> wing) {
		
		if(wing!=null&&!wing.is(PetWing.class)){
			throw new IllegalArgumentException();
		}
		this.wing = wing;
	}
	public static enum PetSlot
	{
		EMPTY(-1),
		HORN(0),
		HEAD(1),
		BODY(2),
		WING(3),
		FOOT(4),
		TAIL(5);

		int value;
		PetSlot(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		}
		
		public static PetSlot byValue(int slotId){
			
			for(PetSlot petSlot:PetSlot.values())
			{
				if(petSlot.value()==slotId){					
					return petSlot;
				}
			}
			return null;
		}		
	}	
	
}