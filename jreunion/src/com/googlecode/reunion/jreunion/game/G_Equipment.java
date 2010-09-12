package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.game.G_Enums.G_EquipmentSlot;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Equipment {
	private G_Weapon weapon;

	private G_Armor helmet;

	private G_Armor armor;

	private G_Armor pants;

	private G_Armor boots;

	private G_Shield shield;

	private G_WandWeapon wand;

	private G_SpecialWeapon specialWeapon;

	private G_Mantle mantle;

	private G_Wing wings;

	private G_Ring ring;

	private G_Necklace necklace;

	private G_Bracelet bracelet;

	public G_Equipment() {
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

	public G_Armor getArmor() {
		return armor;
	}

	public G_Armor getBoots() {
		return boots;
	}

	public G_Bracelet getBracelet() {
		return bracelet;
	}

	public G_Weapon getMainHand() {
		
		return weapon;
	}

	public G_Armor getHelmet() {
		return helmet;
	}

	public G_Item getItem(G_EquipmentSlot slot) {

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

	private G_Mantle getMantle() {
		return mantle;
	}

	public G_Necklace getNecklace() {
		return necklace;
	}

	public G_Armor getPants() {
		return pants;
	}

	public G_Ring getRing() {
		return ring;
	}

	public G_Item getOffHand() {

		if (getShield() != null) {
			return getShield();
		} else if (getWand() != null) {
			return getWand();
		}
		return null;
	}

	private G_Shield getShield() {
		return shield;
	}

	public G_Item getShoulderMount() {

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

	private G_SpecialWeapon getSpecialWeapon() {
		return specialWeapon;
	}

	private G_WandWeapon getWand() {
		return wand;
	}

	private G_Wing getWings() {
		return wings;
	}

	public void setArmor(G_Armor armor) {
		this.armor = armor;
	}

	public void setBoots(G_Armor boots) {
		this.boots = boots;
	}

	public void setBracelet(G_Bracelet bracelet) {
		this.bracelet = bracelet;
	}

	public void setMainhand(G_Weapon weapon) {
		this.weapon = weapon;
	}

	public void setHelmet(G_Armor helmet) {
		this.helmet = helmet;
	}

	public boolean setItem(G_EquipmentSlot slot, G_Item item) {
		switch (slot) {
		case HELMET: {
			if (item instanceof G_Armor || item == null) {
				setHelmet((G_Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case CHEST: {
			if (item instanceof G_Armor || item == null) {
				setArmor((G_Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case PANTS: {
			if (item instanceof G_Armor || item == null) {
				setPants((G_Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case SHOULDER: {
			if (item instanceof G_SpecialWeapon || item instanceof G_Mantle
					|| item instanceof G_Wing || item == null) {
				if (item instanceof G_SpecialWeapon) {
					setShoulderMount(item);
				} else if (item instanceof G_Mantle) {
					setShoulderMount(item);
				} else if (item instanceof G_Wing) {
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
			if (item instanceof G_Armor || item == null) {
				setBoots((G_Armor) item);
				return true;
			} else {
				return false;
			}
		}
		case OFFHAND: {
			if (item instanceof G_Shield || item instanceof G_WandWeapon
					|| item == null) {
				if (item instanceof G_Shield) {
					setOffhand(item);
				} else if (item instanceof G_WandWeapon) {
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
			if (item instanceof G_Necklace || item == null) {
				setNecklace((G_Necklace) item);
				return true;
			} else {
				return false;
			}
		}
		case RING: {
			if (item instanceof G_Ring || item == null) {
				setRing((G_Ring) item);
				return true;
			} else {
				return false;
			}
		}
		case BRACELET: {
			if (item instanceof G_Bracelet || item == null) {
				setBracelet((G_Bracelet) item);
				return true;
			} else {
				return false;
			}
		}
		case MAINHAND: {
			if (item instanceof G_Weapon || item == null) {
				setMainhand((G_Weapon) item);
				return true;
			} else {
				return false;
			}
		}
		}
		return true;

	}

	private void setMantle(G_Mantle mantle) {
		this.mantle = mantle;
	}

	public void setNecklace(G_Necklace necklace) {
		this.necklace = necklace;
	}

	public void setPants(G_Armor pants) {
		this.pants = pants;
	}

	public void setRing(G_Ring ring) {
		this.ring = ring;
	}

	public void setOffhand(G_Item secondHand) {

		if (secondHand instanceof G_Shield) {
			setShield((G_Shield) secondHand);
			setWand(null);
		} else if (secondHand instanceof G_WandWeapon) {
			setWand((G_WandWeapon) secondHand);
			setShield(null);
		} else {
			setShield(null);
			setWand(null);
		}
	}

	private void setShield(G_Shield shield) {
		this.shield = shield;
	}

	public void setShoulderMount(G_Item shoulderMount) {

		if (shoulderMount instanceof G_SlayerWeapon) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon((G_SpecialWeapon) shoulderMount);
		} else if (shoulderMount instanceof G_HeavyWeapon) {
			setMantle(null);
			setWings(null);
			setSpecialWeapon((G_SpecialWeapon) shoulderMount);
		} else if (shoulderMount instanceof G_Mantle) {
			setSpecialWeapon(null);
			setWings(null);
			setMantle((G_Mantle) shoulderMount);
		} else if (shoulderMount instanceof G_Wing) {
			setSpecialWeapon(null);
			setMantle(null);
			setWings((G_Wing) shoulderMount);
		} else {
			setSpecialWeapon(null);
			setMantle(null);
			setWings(null);
		}

	}

	private void setSpecialWeapon(G_SpecialWeapon specialWeapon) {
		this.specialWeapon = specialWeapon;
	}

	private void setWand(G_WandWeapon wand) {
		this.wand = wand;
	}

	private void setWings(G_Wing wings) {
		this.wings = wings;
	}
		
	
}