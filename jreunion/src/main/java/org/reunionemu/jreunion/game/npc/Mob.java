package org.reunionemu.jreunion.game.npc;

import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.game.Enums;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.server.Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Mob extends NpcType {

	private int dmg;

	private int exp;

	private int lime;

	private int attackType;	

	private int speed;

	public Mob(int type) {
		super(type);
		//loadFromReference(type);
	}

	public void attack(LivingObject livingObject) {
		if (getAttackType() == Enums.CLOSE_MELEE_DMG) {
			if (livingObject instanceof Player) {
				closeMeleeAttackPlayer((Player) livingObject);
			}
		} else if (getAttackType() == Enums.RANGE_MELEE_DMG) {
			if (livingObject instanceof Player) {
				rangeMeleeAttackPlayer((Player) livingObject);
			}
		} else if (getAttackType() == Enums.RANGE_MAGIC_DMG) {
			if (livingObject instanceof Player) {
				rangeMagicAttackPlayer((Player) livingObject);
			}
		}
	}

	private void closeMeleeAttackPlayer(Player player) {
		player.setHp(player.getHp() - getDmg());
	}

	public int getAttackType() {
		return attackType;
	}


	public int getDmg() {
		return dmg;
	}

	public int getExp() {
		return exp;
	}

	public int getLime() {
		return lime;
	}
	
	public int getSpeed() {
		return speed;
	}


	@Override
	public void loadFromReference(int id) {
		//super.loadFromReference(id);

		ParsedItem mob = Reference.getInstance().getMobReference()
				.getItemById(id);

		if (mob == null) {
			// cant find Item in the reference continue to load defaults:
			setMaxHp(1);
			setLevel(1);
			setExp(1);
			setLime(1);
			setDmg(0);
			setAttackType(0);
			setMutant(0);
			setNeoProgmare(0);
			setSpeed(1);
			setName("Unknown");
		} else {
			
			if (mob.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setMaxHp(Integer.parseInt(mob.getMemberValue("Hp")));
			} else {
				// use default
				setMaxHp(1);
			}
			
			if (mob.checkMembers(new String[] { "Level" })) {
				// use member from file
				setLevel(Integer.parseInt(mob.getMemberValue("Level")));
			} else {
				// use default
				setLevel(1);
			}
			
			if (mob.checkMembers(new String[] { "Exp" })) {
				// use member from file
				setExp((int) (Integer.parseInt(mob.getMemberValue("Exp")) ));
			} else {
				// use default
				setExp(1);
			}
			if (mob.checkMembers(new String[] { "Lime" })) {
				// use member from file
				setLime((int) (Integer.parseInt(mob.getMemberValue("Lime"))));
			} else {
				// use default
				setLime(1);
			}
			if (mob.checkMembers(new String[] { "Dmg" })) {
				// use member from file
				setDmg(Integer.parseInt(mob.getMemberValue("Dmg")));
			} else {
				// use default
				setDmg(0);
			}
			if (mob.checkMembers(new String[] { "AttackType" })) {
				// use member from file
				setAttackType(Integer
						.parseInt(mob.getMemberValue("AttackType")));
			} else {
				// use default
				setAttackType(0);
			}
			if (mob.checkMembers(new String[] { "DmgType" })) {
				// use member from file
				setDmgType(Integer.parseInt(mob.getMemberValue("DmgType")));
			} else {
				// use default
				setDmgType(0);
			}
			if (mob.checkMembers(new String[] { "Mutant" })) {
				// use member from file
				setMutant(Integer.parseInt(mob.getMemberValue("Mutant")));
			} else {
				// use default
				setMutant(0);
			}
			if (mob.checkMembers(new String[] { "NeoProgmare" })) {
				// use member from file
				setNeoProgmare(Integer.parseInt(mob
						.getMemberValue("NeoProgmare")));
			} else {
				// use default
				setNeoProgmare(0);
			}
			if (mob.checkMembers(new String[] { "Speed" })) {
				// use member from file
				setSpeed(Integer.parseInt(mob.getMemberValue("Speed")));
			} else {
				// use default
				setSpeed(1);
			}
			
			setName(mob.getName());
					
		}
	}

	

	private void rangeMagicAttackPlayer(Player player) {

	}

	private void rangeMeleeAttackPlayer(Player player) {

	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public void setDmg(int dmg) {
		this.dmg = dmg;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setLime(int lime) {
		this.lime = lime;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}