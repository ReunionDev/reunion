package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.game.items.SpecialWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.game.items.equipment.Bracelet;
import com.googlecode.reunion.jreunion.game.items.equipment.HeavyWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Mantle;
import com.googlecode.reunion.jreunion.game.items.equipment.Necklace;
import com.googlecode.reunion.jreunion.game.items.equipment.Shield;
import com.googlecode.reunion.jreunion.game.items.equipment.SlayerWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.WandWeapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Weapon;
import com.googlecode.reunion.jreunion.game.items.equipment.Wing;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Equipment {
	private Weapon weapon;

	private Armor helmet;

	private Armor armor;

	private Armor pants;

	private Armor boots;

	private Shield shield;

	private WandWeapon wand;

	private SpecialWeapon specialWeapon;

	private Mantle mantle;

	private Wing wings;

	private Ring ring;

	private Necklace necklace;

	private Bracelet bracelet;

	public Equipment(Player player) {
		weapon = null;
		helmet = null;
		armor = null;
		pants = null;
		boots = null;
		shield = null;
		wand = null;
		wings = null;
		mantle = null;
		specialWeapon = null;
		ring = null;
		necklace = null;
		bracelet = null;
	}

	public Armor getArmor() {
		return armor;
	}

	public Armor getBoots() {
		return boots;
	}

	public Bracelet getBracelet() {
		return bracelet;
	}

	public Weapon getMainHand() {
		
		return weapon;
	}

	public Armor getHelmet() {
		return helmet;
	}
	
	public int getType(Slot slot){
		Item item = getItem(slot); 
		return item == null ? -1 : item.getType();
	}
	
	public int getEntityId(Slot slot){
		Item item = getItem(slot); 
		return item == null ? -1 : item.getEntityId();
	}
	
	public int getExtraStats(Slot slot){
		Item item = getItem(slot); 
		return item == null ? 0 : item.getExtraStats();
	}
	
	public int getGemNumber(Slot slot){
		Item item = getItem(slot); 
		return item == null ? 0 : item.getGemNumber();
	}

	public Item getItem(Slot slot) {

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

	private Mantle getMantle() {
		return mantle;
	}

	public Necklace getNecklace() {
		return necklace;
	}

	public Armor getPants() {
		return pants;
	}

	public Ring getRing() {
		return ring;
	}

	public Item getOffHand() {

		if (getShield() != null) {
			return getShield();
		} else if (getWand() != null) {
			return getWand();
		}
		return null;
	}

	private Shield getShield() {
		return shield;
	}

	public Item getShoulderMount() {

		if (getSpecialWeapon() != null) {
			return getSpecialWeapon();
		} else if (getMantle() != null) {
			return getMantle();
		} else if (getWings() != null) {
			return getWings();
		} else {
			return null;
		}
	}

	private SpecialWeapon getSpecialWeapon() {
		return specialWeapon;
	}

	private WandWeapon getWand() {
		return wand;
	}

	private Wing getWings() {
		return wings;
	}

	public void setArmor(Armor armor) {
		this.armor = armor;
	}

	public void setBoots(Armor boots) {
		this.boots = boots;
	}

	public void setBracelet(Bracelet bracelet) {
		this.bracelet = bracelet;
	}

	public void setMainhand(Weapon weapon) {
		this.weapon = weapon;
	}

	public void setHelmet(Armor helmet) {
		this.helmet = helmet;
	}

	public boolean setItem(Slot slot, Item item) {
		switch (slot) {
		case HELMET: {
			if (item instanceof Armor || item == null) {
				setHelmet((Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case CHEST: {
			if (item instanceof Armor || item == null) {
				setArmor((Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case PANTS: {
			if (item instanceof Armor || item == null) {
				setPants((Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case SHOULDER: {
			if (item instanceof SpecialWeapon || item instanceof Mantle
					|| item instanceof Wing || item == null) {
				if (item instanceof SpecialWeapon) {
					setShoulderMount(item);
				} else if (item instanceof Mantle) {
					setShoulderMount(item);
				} else if (item instanceof Wing) {
					setShoulderMount(item);
				} else {
					setShoulderMount(item);
				}
				return true;
			} else {
				return false;
			}
		}
		case BOOTS: {
			if (item instanceof Armor || item == null) {
				setBoots((Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case OFFHAND: {
			if (item instanceof Shield || item instanceof WandWeapon
					|| item == null) {
				if (item instanceof Shield) {
					setOffhand(item);
				} else if (item instanceof WandWeapon) {
					setOffhand(item);
				} else {
					setOffhand(item);
				}
				return true;
			} else {
				return false;
			}
		}
		case NECKLACE: {
			if (item instanceof Necklace || item == null) {
				setNecklace((Necklace) item);
				return true;
			} else {
				return false;
			}
		}
		case RING: {
			if (item instanceof Ring || item == null) {
				setRing((Ring) item);
				return true;
			} else {
				return false;
			}
		}
		case BRACELET: {
			if (item instanceof Bracelet || item == null) {
				setBracelet((Bracelet) item);
				return true;
			} else {
				return false;
			}
		}
		case MAINHAND: {
			if (item instanceof Weapon || item == null) {
				setMainhand((Weapon) item);
				return true;
			} else {
				return false;
			}
		}
		}
		return true;

	}

	private void setMantle(Mantle mantle) {
		this.mantle = mantle;
	}

	public void setNecklace(Necklace necklace) {
		this.necklace = necklace;
	}

	public void setPants(Armor pants) {
		this.pants = pants;
	}

	public void setRing(Ring ring) {
		this.ring = ring;
	}

	public void setOffhand(Item secondHand) {

		if (secondHand instanceof Shield) {
			setShield((Shield) secondHand);
			setWand(null);
		} else if (secondHand instanceof WandWeapon) {
			setWand((WandWeapon) secondHand);
			setShield(null);
		} else {
			setShield(null);
			setWand(null);
		}
	}

	private void setShield(Shield shield) {
		this.shield = shield;
	}

	public void setShoulderMount(Item shoulderMount) {

		if (shoulderMount instanceof SlayerWeapon) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon((SpecialWeapon) shoulderMount);
		} else if (shoulderMount instanceof HeavyWeapon) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon((SpecialWeapon) shoulderMount);
		} else if (shoulderMount instanceof Mantle) {
			setSpecialWeapon(null);
			setWings(null);
			setMantle((Mantle) shoulderMount);
		} else if (shoulderMount instanceof Wing) {
			setSpecialWeapon(null);
			setMantle(null);
			setWings((Wing) shoulderMount);
		} else {
			setSpecialWeapon(null);
			setMantle(null);
			setWings(null);
		}

	}

	private void setSpecialWeapon(SpecialWeapon specialWeapon) {
		this.specialWeapon = specialWeapon;
	}

	private void setWand(WandWeapon wand) {
		this.wand = wand;
	}

	private void setWings(Wing wings) {
		this.wings = wings;
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