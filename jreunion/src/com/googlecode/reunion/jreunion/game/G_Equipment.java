package com.googlecode.reunion.jreunion.game;

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

	//private G_Armor shoulderMount;
	
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
		//shoulderMount = null;
		ring = null;
		necklace = null;
		bracelet = null;
	}

	public void setFirstHand(G_Weapon weapon) {
		this.weapon = weapon;
	}

	public G_Weapon getFirstHand() {
		//if(this.weapon == null)
		//	return -1;
		//else
			return this.weapon;
	}

	public void setHelmet(G_Armor helmet){
		this.helmet = helmet;
	}

	public G_Armor getHelmet(){
		return helmet;
	}
	
	public void setArmor(G_Armor armor){
		this.armor = armor;
	}
	
	public G_Armor getArmor(){
		return armor;
	}
	
	public void setPants(G_Armor pants){
		this.pants = pants;
	}
	
	public G_Armor getPants(){
		return pants;
	}
	
	public void setBoots(G_Armor boots){
		this.boots = boots;
	}
	
	public G_Armor getBoots(){
		return boots;
	}
	
	public void setSecondHand(G_Item secondHand){

		if(secondHand instanceof G_Shield){
			setShield((G_Shield)secondHand);
			setWand(null);
		}
		else if(secondHand instanceof G_WandWeapon){
				setWand((G_WandWeapon)secondHand);
				setShield(null);
			 }
		else {
			setShield(null);
			setWand(null);
		}
	}
	
	private void setShield(G_Shield shield){
		this.shield = shield;
	}
	
	private void setWand(G_WandWeapon wand){
		this.wand = wand;
	}
	
	public G_Item getSecondHand(){

		if(getShield() != null)
			return getShield();
		else if(getWand() != null)
			return getWand();
		return null;
	}
	
	private G_Shield getShield(){
		return shield;
	}
	
	private G_WandWeapon getWand(){
		return wand;
	}
	
	public void setShoulderMount(G_Item shoulderMount){

		if(shoulderMount instanceof G_SlayerWeapon){
			setMantle(null);
			setWings(null);
			setSpecialWeapon((G_SpecialWeapon)shoulderMount);
		}
		else if(shoulderMount instanceof G_HeavyWeapon){
				setMantle(null);
				setWings(null);
				setSpecialWeapon((G_SpecialWeapon)shoulderMount);
			 }
		else if(shoulderMount instanceof G_Mantle){
			 	setSpecialWeapon(null);
			 	setWings(null);
			 	setMantle((G_Mantle)shoulderMount);
		 	  }
		else if(shoulderMount instanceof G_Wing){
				setSpecialWeapon(null);
				setMantle(null);
				setWings((G_Wing)shoulderMount);
			 }
		else {
			 	setSpecialWeapon(null);
			 	setMantle(null);
			 	setWings(null);
		 	  }
		
	}
	public G_Item getShoulderMount(){
		
		if (getSpecialWeapon() != null) 
			return getSpecialWeapon();
		else if (getMantle() != null)
				return getMantle();
			 else if (getWings() != null)
					 return getWings();
				  else
					  return null;
	}

	private void setSpecialWeapon(G_SpecialWeapon specialWeapon){
		//specialWeapon.loadFromReference(specialWeapon.getType());
		this.specialWeapon = specialWeapon;
	}
	
	private G_SpecialWeapon getSpecialWeapon(){
		return this.specialWeapon;
	}
	
	private void setMantle(G_Mantle mantle){
		this.mantle = mantle;
	}
	
	private G_Mantle getMantle(){
		return this.mantle;
	}
	
	private void setWings(G_Wing wings){
		this.wings = wings;
	}
	
	private G_Wing getWings(){
		return this.wings;
	}
	
	public void setRing(G_Ring ring) {
		this.ring = ring;
	}

	public G_Ring getRing() {
		return this.ring;
	}

	public void setNecklace(G_Necklace necklace) {
		this.necklace = necklace;
	}

	public G_Necklace getNecklace() {
		return this.necklace;
	}

	public void setBracelet(G_Bracelet bracelet) {
		this.bracelet = bracelet;
	}

	public G_Bracelet getBracelet() {
		return this.bracelet;
	}
	public boolean setItem(int slotid, G_Item item )
	{
		switch (slotid)
		{
			case G_Enums.SLOT_HEAD:	
			{
				if (item instanceof G_Armor || item==null)
				{
					setHelmet((G_Armor)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_TOP:	
			{
				if (item instanceof G_Armor || item==null)
				{
					setArmor((G_Armor)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_LEG:	
			{
				if (item instanceof G_Armor || item==null)
				{
					setPants((G_Armor)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_SHOULDER_MOUNT:	
			{
				if (item instanceof G_SpecialWeapon ||
						item instanceof G_Mantle ||
						item instanceof G_Wing || item==null)
				{
					if(item instanceof G_SpecialWeapon)
						setShoulderMount((G_SpecialWeapon)item);
					else if(item instanceof G_Mantle)
						setShoulderMount((G_Mantle)item);
					else if(item instanceof G_Wing)
						setShoulderMount((G_Wing)item);
					else
						setShoulderMount(item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_FEET:	
			{
				if (item instanceof G_Armor || item==null)
				{
					setBoots((G_Armor)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_SECOND_HAND:	
			{
				if (item instanceof G_Shield ||
						item instanceof G_WandWeapon || item==null)
				{
					if(item instanceof G_Shield)
						setSecondHand((G_Shield)item);
					else if(item instanceof G_WandWeapon)
						setSecondHand((G_WandWeapon)item);
					else
						setSecondHand(item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_NECKLACE:	
			{
				if (item instanceof G_Necklace || item==null)
				{
					setNecklace((G_Necklace)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_RING:	
			{
				if (item instanceof G_Ring || item==null)
				{
					setRing((G_Ring)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_BRACELET:	
			{
				if (item instanceof G_Bracelet || item==null)
				{
					setBracelet((G_Bracelet)item);
					return true;
				}
				else return false;
			}
			case G_Enums.SLOT_FIRST_HAND:	
			{
				if (item instanceof G_Weapon || item==null)
				{
					this.setFirstHand((G_Weapon)item);
					return true;
				}
				else return false;
			}
		}
		return true;
		
	}
	public G_Item getItem(int slotid)
	{
		
		switch (slotid)
		{

		case G_Enums.SLOT_BRACELET:	return getBracelet();
		case G_Enums.SLOT_FEET:	return getBoots();
		case G_Enums.SLOT_FIRST_HAND: return getFirstHand();
		case G_Enums.SLOT_HEAD:	return  getHelmet();
		case G_Enums.SLOT_LEG: return getPants();
		case G_Enums.SLOT_NECKLACE: return getNecklace();
		case G_Enums.SLOT_RING: return getRing();
		case G_Enums.SLOT_SECOND_HAND: return getSecondHand();
		case G_Enums.SLOT_SHOULDER_MOUNT: return getShoulderMount();
		case G_Enums.SLOT_TOP: return getArmor();
		}
		
		return null;
	}
}