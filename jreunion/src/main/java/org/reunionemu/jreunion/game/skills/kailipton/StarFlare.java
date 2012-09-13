package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.List;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.KailiptonPlayer;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.StaffWeapon;
import org.reunionemu.jreunion.game.items.equipment.Weapon;
import org.reunionemu.jreunion.game.skills.Modifier;
import org.reunionemu.jreunion.game.skills.Modifier.ValueType;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class StarFlare extends Tier3 implements Castable, Effectable {

	public StarFlare(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.FIRE;
	}
	
	public float getDamageModifier(){
		/* level 1 = 6 (magic damage)
		 * level 2 = 18
		 * level 3 = 21
		 * ...
		 * level 25 = 160
		 */
		
		return (float)154/(getMaxLevel()-1);
		
	}
	
	public long getDamageModifier(Player player){
		
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (6+((level-1)*getDamageModifier()));			
			}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 57
		 * level 2 = 59
		 * level 3 = 62
		 * ...
		 * level 25 = 121
		 */
		return 64f/(getMaxLevel()-1);
	}
	
	public long getManaModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (57 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims, int castStep) {
		if(caster instanceof KailiptonPlayer){
			Player player = (Player)caster;
			long currentMana = player.getMana();
			long manaSpent = getManaModifier(player);
			
			player.setMana(currentMana - manaSpent);
			
			Item<?> item = player.getEquipment().getMainHand();
			long baseDamage = player.getBaseDamage();
			long weaponDamage = 0;
			double weaponMagicBoost=1;
			float criticalMultiplier = 0;
			Weapon weapon = null;
			
			if(item!=null && item.is(StaffWeapon.class)){
				weapon = (Weapon)item.getType();
				criticalMultiplier = weapon.getCritical();
				weaponDamage += weapon.getDamage(item);
				weaponMagicBoost += weapon.getMagicDmg(item); // % of magic dmg boost
			}
			
			long fireDamage = getDamageModifier(player);
			float fireMasteryDamage = 1;
			
			// calculate damage from skills FireBall, FirePillar and FireMastery
			for(Skill skill: ((Player)caster).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // FireMastery
									fireMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE: // FileBall and FirePillar
									if(skill instanceof FireBall){
										fireDamage += (modifier.getModifier(caster)*0.5);
									}
									else if(skill instanceof FirePillar){
										fireDamage += (modifier.getModifier(caster)*0.7);
									}
									break;
							}
						}
					}						
				}
			}
			
			long magicDamage = (long) ((baseDamage + weaponDamage + fireDamage)
					* fireMasteryDamage * weaponMagicBoost * (criticalMultiplier+1));
			
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			//Todo: this skill can target up to 5 targets
			//(Main target 100% damage, other targets 80% damage)
			synchronized(victims){
				int victimCount = 1;
				for(LivingObject victim : victims){
					//if its 1st victim apply 100% dmg, if not is only 80% dmg
					magicDamage *= (victimCount++ == 2) ? 0.8 : 1;
					victim.getsAttacked(player, magicDamage, true);
					player.getClient().sendPacket(Type.AV, victim, player.getDmgType());
				}
				return true;
			}
		}		
		return false;
	}
	
	public void effect(LivingObject source, LivingObject target, int castStep){
		source.getInterested().sendPacket(Type.EFFECT, source, target , this,0,0,0);
	}
	
	public int getEffectModifier() {
		return 0;
	}
}