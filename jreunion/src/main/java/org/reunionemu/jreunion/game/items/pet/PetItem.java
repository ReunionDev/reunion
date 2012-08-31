package org.reunionemu.jreunion.game.items.pet;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PetItem extends ItemType {
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

	public PetItem(int type) {
		super(type);
	}

	public int getAssembLevel() {
		return assembLevel;
	}

	public int getBuyExp() {
		return buyExp;
	}

	public int getCloseAtk() {
		return closeAtk;
	}

	public int getCloseDef() {
		return closeDef;
	}

	public int getDistAtk() {
		return distAtk;
	}

	public int getDistDef() {
		return distDef;
	}

	public int getHp() {
		return hp;
	}

	public int getReqLevel() {
		return reqLevel;
	}

	public int getSpeed() {
		return speed;
	}

	public int getType() {
		return type;
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);

		ParsedItem item = Reference.getInstance().getItemReference()
				.getItemById(id);

		if (item == null) {
			// cant find Item in the reference continue to load defaults:
			setHp(0);
			setBuyExp(0);
			setCloseDef(0);
			setDistDef(0);
			setCloseAtk(0);
			setDistAtk(0);
			setSpeed(0);
			setAssembLevel(0);
			setReqLevel(0);
		} else {
			if (item.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setHp(Integer.parseInt(item.getMemberValue("Hp")));
			} else {
				// use default
				setHp(0);
			}
			if (item.checkMembers(new String[] { "BuyExp" })) {
				// use member from file
				setBuyExp(Integer
						.parseInt(item.getMemberValue("BuyExp")));
			} else {
				// use default
				setBuyExp(0);
			}
			if (item.checkMembers(new String[] { "CloseDef" })) {
				// use member from file
				setCloseDef(Integer.parseInt(item
						.getMemberValue("CloseDef")));
			} else {
				// use default
				setCloseDef(0);
			}
			if (item.checkMembers(new String[] { "DistDef" })) {
				// use member from file
				setDistDef(Integer.parseInt(item
						.getMemberValue("DistDef")));
			} else {
				// use default
				setDistDef(0);
			}
			if (item.checkMembers(new String[] { "CloseAtk" })) {
				// use member from file
				setCloseAtk(Integer.parseInt(item
						.getMemberValue("CloseAtk")));
			} else {
				// use default
				setCloseAtk(0);
			}
			if (item.checkMembers(new String[] { "DistAtk" })) {
				// use member from file
				setDistAtk(Integer.parseInt(item
						.getMemberValue("DistAtk")));
			} else {
				// use default
				setDistAtk(0);
			}
			if (item.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpeed(Integer.parseInt(item.getMemberValue("Speed")));
			} else {
				// use default
				setSpeed(0);
			}
			if (item.checkMembers(new String[] { "AssembLevel" })) {
				// use member from file
				setAssembLevel(Integer.parseInt(item
						.getMemberValue("AssembLevel")));
			} else {
				// use default
				setAssembLevel(0);
			}
			if (item.checkMembers(new String[] { "ReqLevel" })) {
				// use member from file
				setReqLevel(Integer.parseInt(item
						.getMemberValue("ReqLevel")));
			} else {
				// use default
				setReqLevel(0);
			}
		}
	}

	public void setAssembLevel(int assembLevel) {
		this.assembLevel = assembLevel;
	}

	public void setBuyExp(int buyExp) {
		this.buyExp = buyExp;
	}

	public void setCloseAtk(int closeAtk) {
		this.closeAtk = closeAtk;
	}

	public void setCloseDef(int closeDef) {
		this.closeDef = closeDef;
	}

	public void setDistAtk(int distAtk) {
		this.distAtk = distAtk;
	}

	public void setDistDef(int distDef) {
		this.distDef = distDef;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setReqLevel(int reqLevel) {
		this.reqLevel = reqLevel;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}