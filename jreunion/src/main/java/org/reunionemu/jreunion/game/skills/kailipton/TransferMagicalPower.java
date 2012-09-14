package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;
import org.reunionemu.jreunion.game.items.equipment.WandWeapon;

public class TransferMagicalPower extends Skill {

	public TransferMagicalPower(SkillManager skillManager, int id) {
		super(skillManager, id);
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 159+skillLevel;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}

	public float getDamageModifier() {
		/*
		 * lvl 1 = 5% lvl
		 * lvl 2 = 
		 * lvl 3 = 
		 * lvl 4 = 200%
		 */

		return 1.95f / (getMaxLevel() - 1);
	}

	public float getDamageModifier(Player player) {

		float modifier = 1;

		int level = player.getSkillLevel(this);
		if (level > 0) {
			modifier += (0.05 + ((level - 1) * getDamageModifier()));
		}

		return modifier;
	}

	public boolean cast(LivingObject caster, List<LivingObject> targets, int unknown1) {

		Player player = null;

		if (caster instanceof Player) {
			player = (Player) caster;
		}

		Item<?> offHandEquipment = player.getEquipment().getOffHand();
		if(offHandEquipment == null){
			LoggerFactory.getLogger(TransferMagicalPower.class).error("It wasn't possible to get player OffHand equipment.");
			return false;
		}
		offHandEquipment.use(caster, -1, 0);

		WandWeapon wandWeapon = null;

		if (offHandEquipment.getType() instanceof WandWeapon) {
			wandWeapon = (WandWeapon) offHandEquipment.getType();
		} else {
			LoggerFactory.getLogger(TransferMagicalPower.class).error("It's not possible to cast. Not wearing a wand.");
			return false;
		}
		
		long bestAttack = player.getBestAttack();
		long wandDmg = wandWeapon.getDamage();
		float accumulatedDmg = wandWeapon.getAccumulatedDmg();
		float criticalMultiplier = wandWeapon.getCritical();
		

		// not sure of this formula : damage wand = Attack + Wand dmg + Acumulated Damage
		//long damage = bestAttack + wandDmg + (long) (accumulatedDmg);
		long damage = (bestAttack + wandDmg) * (long)getDamageModifier(player);
		
		if(damage > accumulatedDmg){
			damage = (long)accumulatedDmg;
		}
		
		damage += (long)(damage * criticalMultiplier);

		// now increasing accumulated damage (max should be 20 * original_accu_damage)
		//if (accumulatedDmg < (20 * accumulatedDmg_original)) {
		//	float new_value = accumulatedDmg + accumulatedDmg_original;
		//	wandWeapon.setAccumulatedDmg(new_value);
		//}

		synchronized (targets) {
			for (LivingObject target : targets) {
				long newHp = Tools.between(target.getHp() - damage, 0l,	target.getMaxHp());

				if (newHp <= 0) {
					LoggerFactory.getLogger(TransferMagicalPower.class).info("Player " + player + " killed npc " + target);
					if (target instanceof Npc) {
						((Npc<?>) target).kill(player);
					}
				} else {
					target.setHp(newHp);
				}
				
				player.getClient().sendPacket(Type.SAV, target,
						criticalMultiplier > 0 ? 1 : 0, unknown1,
								offHandEquipment.getGemNumber(), 10);
			}
		}

		player.clearAttackQueue();

		return true;

	}
}