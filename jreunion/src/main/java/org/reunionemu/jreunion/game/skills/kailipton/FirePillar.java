package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.List;
import java.util.Vector;

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
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

public class FirePillar extends Tier2 implements Castable, Modifier, Effectable {

	public FirePillar(SkillManager skillManager,int id) {
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
		 * level 25 = 150
		 */
		
		return 144f/(getMaxLevel()-1);
		
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
		 * level 1 = 22
		 * level 2 = 23
		 * level 3 = 25
		 * ...
		 * level 25 = 66
		 */
		return 44f/(getMaxLevel()-1);
	}
	
	public long getManaModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (22 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments) {
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
			
			// calculate damage from skills FireBall and FireMastery
			for(Skill skill: ((Player)caster).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getValueType() == getValueType()){	
							switch(modifier.getModifierType()){	
								case MULTIPLICATIVE: // FireMastery
									fireMasteryDamage *= modifier.getModifier(caster);
									break;
								case ADDITIVE: // FireBall
									fireDamage += (modifier.getModifier(caster)*0.6);
									break;
							}
						}
					}						
				}
			}
			
			long magicDamage = (long) ((baseDamage + weaponDamage + fireDamage)
					* fireMasteryDamage * weaponMagicBoost * (criticalMultiplier+1));
			
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			int victimTarget = 1;
			
			for(int i=3; i < arguments.length-1; i++){
				if(victim.getEntityId() == Integer.parseInt(arguments[i])){
					break;
				}
				victimTarget++;
			}
			
			//This skill can target up to 4 targets
			//(Main target 100% damage, other targets 70% damage)
			synchronized(victim){
				//if its 1st victim apply 100% dmg, if not, apply only 70% dmg
				magicDamage *= (victimTarget > 1 ? 0.7 : 1);
				victim.getsAttacked(player, magicDamage, true);
				player.getClient().sendPacket(Type.AV, victim, player.getDmgType());
				return true;
			}	
		}		
		return false;
	}
	
	public boolean getCondition(LivingObject owner){
		if(owner instanceof Player){
			Player player = (Player)owner;
			if(player.getSkillLevel(this)==0)
				return false;
			return true;
		}
		return false;
	}

	@Override
	public ModifierType getModifierType() {

		return Modifier.ModifierType.ADDITIVE;
	}
	
	private int [] affectedSkillIds = {26};
	private List<Skill>  affectedSkills = null ;
	
	@Override
	public List<Skill> getAffectedSkills() {
		synchronized(affectedSkillIds){
			if (affectedSkills==null){
				affectedSkills = new Vector<Skill>();
				for(int skillId:affectedSkillIds){					
					SkillManager skillManager = getSkillManager();
					affectedSkills.add(skillManager.getSkill(skillId));					
				}
			}		
		}		
		return affectedSkills;
	}

	@Override
	public float getModifier(LivingObject livingObject) {
		return getDamageModifier((Player)livingObject);
	}

	public void effect(LivingObject source, LivingObject target, String[] arguments){
		source.getInterested().sendPacket(Type.EFFECT, source, target , this,0,0,0);
	}
	
	public int getEffectModifier() {
		return 0;
	}
	
	@Override
	public List<LivingObject> getTargets(String[] arguments, LocalMap map){
		return getMultipleTargets(arguments, 3, arguments.length-1, map);
	}
}