package org.reunionemu.jreunion.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.items.SpecialWeapon;
import org.reunionemu.jreunion.game.items.equipment.Armor;
import org.reunionemu.jreunion.game.items.equipment.Bracelet;
import org.reunionemu.jreunion.game.items.equipment.HeavyWeapon;
import org.reunionemu.jreunion.game.items.equipment.Mantle;
import org.reunionemu.jreunion.game.items.equipment.Necklace;
import org.reunionemu.jreunion.game.items.equipment.Ring;
import org.reunionemu.jreunion.game.items.equipment.Shield;
import org.reunionemu.jreunion.game.items.equipment.SlayerWeapon;
import org.reunionemu.jreunion.game.items.equipment.WandWeapon;
import org.reunionemu.jreunion.game.items.equipment.Weapon;
import org.reunionemu.jreunion.game.items.equipment.Wing;
import org.reunionemu.jreunion.game.items.pet.PetEgg;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Equipment {
	private Item<?> weapon;

	private Item<?> helmet;

	private Item<?> armor;

	private Item<?> pants;

	private Item<?> boots;

	private Item<?> shield;

	private Item<?> wand;

	private Item<?> specialWeapon;

	private Item<?> mantle;

	private Item<?> wings;

	private Item<?> ring;

	private Item<?> necklace;

	private Item<?> bracelet;
	
	private Item<?> egg;
	
	private Player owner;

	public Equipment(Player player) {
		setOwner(player);
	}

	public Item<?> getArmor() {
		return armor;
	}

	public Item<?> getBoots() {
		return boots;
	}

	public Item<?> getBracelet() {
		return bracelet;
	}

	public Item<?> getMainHand() {
		
		return weapon;
	}

	public Item<?> getHelmet() {
		return helmet;
	}
	
	public int getTypeId(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? -1 : item.getType().getTypeId();
	}
	
	public int getEntityId(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? -1 : item.getEntityId();
	}
	
	public long getExtraStats(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getExtraStats();
	}
	
	public long getGemNumber(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getGemNumber();
	}
	
	public int getDurability(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getDurability();
	}
	
	public int getMaxDurability(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getType().getMaxDurability();
	}
	
	public int getUnknown1(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getUnknown1();
	}
	
	public int getUnknown2(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getUnknown2();
	}
	
	public int getUnknown3(Slot slot){
		Item<?> item = getItem(slot); 
		return item == null ? 0 : item.getUnknown3();
	}

	public Item<?> getItem(Slot slot) {

		switch (slot) {
		case BRACELET:
			return getBracelet();
		case BOOTS:
			return getBoots();
		case MAINHAND:
			return getMainHand();
		case HELMET:
			return getHelmet();
		case PANTS:
			return getPants();
		case NECKLACE:
			return getNecklace();
		case RING:
			return getRing();
		case OFFHAND:
			return getOffHand();
		case SHOULDER:
			return getShoulderMount();
		case CHEST:
			return getArmor();
		}

		return null;
	}

	private Item<?> getMantle() {
		return mantle;
	}

	public Item<?> getNecklace() {
		return necklace;
	}

	public Item<?> getPants() {
		return pants;
	}

	public Item<?> getRing() {
		return ring;
	}

	public Item<?> getOffHand() {

		if (getShield() != null) {
			return getShield();
		} else if (getWand() != null) {
			return getWand();
		}
		return null;
	}

	private Item<?> getShield() {
		return shield;
	}

	public Item<?> getShoulderMount() {

		if (getSpecialWeapon() != null) {
			return getSpecialWeapon();
		} else if (getMantle() != null) {
			return getMantle();
		} else if (getWings() != null) {
			return getWings();
		} else if (getEgg() != null) {
			return getEgg();
		} else {
			return null;
		}
	}

	private Item<?> getSpecialWeapon() {
		return specialWeapon;
	}

	private Item<?> getWand() {
		return wand;
	}

	private Item<?> getWings() {
		return wings;
	}

	public void setArmor(Item<?> armor) {
		if(armor!=null&&!armor.is(Armor.class)){
			throw new IllegalArgumentException();
		}
		this.armor = armor;
	}

	public void setBoots(Item<?> boots) {
		if(boots!=null&&!boots.is(Armor.class)){
			throw new IllegalArgumentException();
		}
		this.boots = boots;
	}

	public void setBracelet(Item<?> bracelet) {
		if(bracelet!=null&&!bracelet.is(Bracelet.class)){
			throw new IllegalArgumentException();
		}
		this.bracelet = bracelet;
	}

	public void setMainhand(Item<?> weapon) {
		if(weapon!=null&&!weapon.is(Weapon.class)){
			throw new IllegalArgumentException();
		}
		this.weapon = weapon;
	}

	public void setHelmet(Item<?> helmet) {
		if(helmet!=null&&!helmet.is(Armor.class)){
			throw new IllegalArgumentException();
		}
		this.helmet = helmet;
	}

	public boolean setItem(Slot slot, Item<?> item) {
		try{
			switch (slot) {
			case HELMET: {
				setHelmet(item);
				break;
			}
			case CHEST: {
				setArmor(item);
				break;
			}
			case PANTS: {
				setPants(item);
				break;
			}
			case SHOULDER: {
				setShoulderMount(item);
				break;
			}
			case BOOTS: {
				setBoots(item);
				break;
			}
			case OFFHAND: {
				setOffhand(item);
				break;
			}
			case NECKLACE: {
				setNecklace(item);
				break;
			}
			case RING: {
				setRing(item);
				break;
			}
			case BRACELET: {
				setBracelet(item);
				break;
			}
			case MAINHAND: {
				setMainhand(item);
				break;
			}
			}
		}catch(IllegalAccessError e){
			return false;
		}
		return true;

	}

	private void setMantle(Item<?> mantle) {
		if(mantle!=null&&!mantle.is(Mantle.class)){
			throw new IllegalArgumentException();
		}
		this.mantle = mantle;
	}

	public void setNecklace(Item<?> necklace) {
		if(necklace!=null&&!necklace.is(Necklace.class)){
			throw new IllegalArgumentException();
		}
		this.necklace = necklace;
	}

	public void setPants(Item<?> pants) {
		if(pants!=null&&!pants.is(Armor.class)){
			throw new IllegalArgumentException();
		}
		this.pants = pants;
	}

	public void setRing(Item<?> ring) {
		if(ring!=null&&!ring.is(Ring.class)){
			throw new IllegalArgumentException();
		}
		this.ring = ring;
	}

	public void setOffhand(Item<?> secondHand) {
		try{
			if (secondHand.is(Shield.class)) {
				setShield(secondHand);
				setWand(null);
			} else if (secondHand.is(WandWeapon.class)) {
				setWand(secondHand);
				setShield(null);
			} else {
				setShield(null);
				setWand(null);
			}
		} catch (NullPointerException e)
		{
			LoggerFactory.getLogger(Equipment.class).error("Player has wrong item");
		}
	}

	private void setShield(Item<?> shield) {
		if(shield!=null&&!shield.is(Shield.class)){
			throw new IllegalArgumentException();
		}
		this.shield = shield;
	}

	public void setShoulderMount(Item<?> shoulderMount) {

		if(shoulderMount == null) {
			setSpecialWeapon(null);
			setMantle(null);
			setWings(null);
			setEgg(null);
		} else if (shoulderMount.is(SlayerWeapon.class)) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon(shoulderMount);
			setEgg(null);
		} else if (shoulderMount.is(HeavyWeapon.class)) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon(shoulderMount);
			setEgg(null);
		} else if (shoulderMount.is(Mantle.class)) {
			setSpecialWeapon(null);
			setWings(null);
			setMantle(shoulderMount);
			setEgg(null);
		} else if (shoulderMount.is(Wing.class)) {
			setSpecialWeapon(null);
			setMantle(null);
			setWings(shoulderMount);
			setEgg(null);
		} else if (shoulderMount.is(PetEgg.class)) {
			setSpecialWeapon(null);
			setMantle(null);
			setWings(null);
			setEgg(shoulderMount);
		}

	}

	private void setSpecialWeapon(Item<?> specialWeapon) {
		if(specialWeapon!=null&&!specialWeapon.is(SpecialWeapon.class)){
			throw new IllegalArgumentException();
		}
		this.specialWeapon = specialWeapon;
	}

	private void setWand(Item<?> wand) {
		if(wand!=null&&!wand.is(WandWeapon.class)){
			throw new IllegalArgumentException();
		}
		this.wand = wand;
	}

	private void setWings(Item<?> wings) {
		if(wings!=null&&!wings.is(Wing.class)){
			throw new IllegalArgumentException();
		}
		this.wings = wings;
	}
	public Item<?> getEgg() {
		return egg;
	}

	public void setEgg(Item<?> egg) {
		if(egg!=null&&!egg.is(PetEgg.class)){
			throw new IllegalArgumentException();
		}
		this.egg = egg;
	}
	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
	public static enum Slot
	{
		EMPTY(-1),
		HELMET(0),
		CHEST(1),
		PANTS(2),
		SHOULDER(3),
		BOOTS(4),
		OFFHAND(5),
		NECKLACE(6),
		RING(7),		
		BRACELET(8),
		MAINHAND(9);

		int value;
		Slot(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		
		}
		
		public static Slot byValue(int slotId){
			
			for(Slot slot:Slot.values())
			{
				if(slot.value()==slotId){					
					return slot;
				}
			}
			return null;
		}		
	}	
	
}