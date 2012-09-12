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

public class StoneLance extends Tier3 implements Castable, Effectable {

	public StoneLance(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	public ValueType getValueType() {
		return Modifier.ValueType.EARTH;
	}
	
	public float getDamageModifier(){
		/* level 1 = 6 (magic damage)
		 * level 2 = 18
		 * level 3 = 21
		 * ...
		 * level 25 = 155
		 */
		
		return (float)149/(getMaxLevel()-1);
		
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
		 * level 1 = 21
		 * level 2 = 22
		 * level 3 = 23
		 * ...
		 * level 25 = 45
		 */
		return 24f/(getMaxLevel()-1);
	}
	
	public long getManaModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (21 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
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
			
			long stoneDamage = getDamageModifier(player);
			float stoneMasteryDamage = 1;
			
			// calculate damage of skill PebbleShot, Hail and StoneMastery
			for(Skill skill: ((Player)caster).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // StoneMastery
									stoneMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE: // PebbleShot / Hail
									if(skill instanceof PebbleShot){
										stoneDamage += (modifier.getModifier(caster)*0.5);
									}
									else if(skill instanceof Hail){
										stoneDamage += (modifier.getModifier(caster)*0.7);
									}
									break;
							}
						}
					}						
				}
			}
			
			long magicDamage = (long) ((baseDamage + weaponDamage + stoneDamage)
					* stoneMasteryDamage * weaponMagicBoost * (criticalMultiplier+1));
			
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			synchronized(victims){
				for(LivingObject victim : victims){ 
					victim.getsAttacked(player, magicDamage);
					player.getClient().sendPacket(Type.AV, victim, player.getDmgType());
				}
				return true;
			}
		}		
		return false;
	}
	
	public void effect(LivingObject source, LivingObject target){
		source.getInterested().sendPacket(Type.EFFECT, source, target , this,0,0,0);
	}
	
	public int getEffectModifier() {
		return 0;
	}
}