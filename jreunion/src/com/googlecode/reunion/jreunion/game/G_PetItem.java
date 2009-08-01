package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_PetItem extends G_Item {
	private int type;

	private int buyExp;

	private int hp;

	private int closeDef;

	private int distDef;

	private int closeAtk;

	private int distAtk;

	private int speed;

	private int assembLevel;

	private int reqLevel;

	public G_PetItem(int type) {
		super(type);
	}

	public int getPetItemType() {
		return this.type;
	}

	public void setPetItemBuyExp(int buyExp) {
		this.buyExp = buyExp;
	}

	public int getPetItemBuyExp() {
		return this.buyExp;
	}

	public void setPetItemHp(int hp) {
		this.hp = hp;
	}

	public int getPetItemHp() {
		return this.hp;
	}
	public void setPetItemCloseDef(int closeDef) {
		this.closeDef = closeDef;
	}

	public int getPetItemCloseDef() {
		return this.closeDef;
	}

	public void setPetItemDistDef(int distDef) {
		this.distDef = distDef;
	}

	public int getPetItemDistDef() {
		return this.distDef;
	}

	public void setPetItemCloseAtk(int closeAtk) {
		this.closeAtk = closeAtk;
	}

	public int getPetItemCloseAtk() {
		return this.closeAtk;
	}

	public void setPetItemDistAtk(int distAtk) {
		this.distAtk = distAtk;
	}

	public int getPetItemDistAtk() {
		return this.distAtk;
	}

	public void setPetItemSpeed(int speed) {
		this.speed = speed;
	}

	public int getPetItemSpeed() {
		return this.speed;
	}

	public void setPetItemAssembLevel(int assembLevel) {
		this.assembLevel = assembLevel;
	}

	public int getPetItemAssembLevel() {
		return this.assembLevel;
	}

	public void setPetItemReqLevel(int reqLevel) {
		this.reqLevel = reqLevel;
	}

	public int getPetItemReqLevel() {
		return this.reqLevel;
	}
	public void loadFromReference(int id) 
	{
		super.loadFromReference(id);
		
		S_ParsedItem item = S_Reference.getInstance().getItemReference().getItemById(id);
		
	  if (item==null)
	  {
		// cant find Item in the reference continue to load defaults:
		  setPetItemHp(0);
		  setPetItemBuyExp(0);
		  setPetItemCloseDef(0);
		  setPetItemDistDef(0);
		  setPetItemCloseAtk(0);
		  setPetItemDistAtk(0);
		  setPetItemSpeed(0);
		  setPetItemAssembLevel(0);
		  setPetItemReqLevel(0);
	  }
	  else {
		if(item.checkMembers(new String[]{"Hp"}))
		{
			// use member from file
			setPetItemHp(Integer.parseInt(item.getMemberValue("Hp")));
		}
		else
		{
			// use default
			setPetItemHp(0);
		}
		if(item.checkMembers(new String[]{"BuyExp"}))
		{
			// use member from file
			setPetItemBuyExp(Integer.parseInt(item.getMemberValue("BuyExp")));
		}
		else
		{
			// use default
			setPetItemBuyExp(0);
		}
		if(item.checkMembers(new String[]{"CloseDef"}))
		{
			// use member from file
			setPetItemCloseDef(Integer.parseInt(item.getMemberValue("CloseDef")));
		}
		else
		{
			// use default
			setPetItemCloseDef(0);
		}
		if(item.checkMembers(new String[]{"DistDef"}))
		{
			// use member from file
			setPetItemDistDef(Integer.parseInt(item.getMemberValue("DistDef")));
		}
		else
		{
			// use default
			setPetItemDistDef(0);
		}
		if(item.checkMembers(new String[]{"CloseAtk"}))
		{
			// use member from file
			setPetItemCloseAtk(Integer.parseInt(item.getMemberValue("CloseAtk")));
		}
		else
		{
			// use default
			setPetItemCloseAtk(0);
		}
		if(item.checkMembers(new String[]{"DistAtk"}))
		{
			// use member from file
			setPetItemDistAtk(Integer.parseInt(item.getMemberValue("DistAtk")));
		}
		else
		{
			// use default
			setPetItemDistAtk(0);
		}
		if(item.checkMembers(new String[]{"Speed"}))
		{
			// use member from file
			setPetItemSpeed(Integer.parseInt(item.getMemberValue("Speed")));
		}
		else
		{
			// use default
			setPetItemSpeed(0);
		}
		if(item.checkMembers(new String[]{"AssembLevel"}))
		{
			// use member from file
			setPetItemAssembLevel(Integer.parseInt(item.getMemberValue("AssembLevel")));
		}
		else
		{
			// use default
			setPetItemAssembLevel(0);
		}
		if(item.checkMembers(new String[]{"ReqLevel"}))
		{
			// use member from file
			setPetItemReqLevel(Integer.parseInt(item.getMemberValue("ReqLevel")));
		}
		else
		{
			// use default
			setPetItemReqLevel(0);
		}
	  }
	}
}