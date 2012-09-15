package org.reunionemu.jreunion.game.skills;

import java.util.Collections;
import java.util.List;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.equipment.Weapon;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.SkillManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAttack extends Skill implements Castable, Effectable{
	
	public BasicAttack(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	private static Logger logger = LoggerFactory.getLogger(BasicAttack.class);
	
	@Override
	public void handle(Player player, String[] arguments) {

		Client client = player.getClient();
		int entityId = Integer.parseInt(arguments[2]);

		LivingObject target = (LivingObject) player.getPosition().getLocalMap().getEntity(entityId);

			
		if(arguments[1].equals("npc")||arguments[1].equals("n")){			

			
		}else if(arguments[1].equals("char")||arguments[1].equals("c")){
			
			client.sendPacket(Type.SAY, "No PVP implemented yet!");
			
		}else{
			
			logger.error("invalid attack arguments");
		}		
		
		List<LivingObject> victims = Collections.singletonList(target);		
				
		if(this.cast(player, victims, 0)){
			this.effect(player, target, 0);
		}	
		
	}
	
	
	public boolean cast(LivingObject attacker, List<LivingObject> victims, int castStep) {
		
		float damage = 0;
		
		if (attacker instanceof Player){
			Player player = (Player)attacker; 
			Item<?> item = player.getEquipment().getMainHand();
			Weapon weapon = null;
			float baseDamage = player.getBaseDamage();
			float weaponDamage = 0;
			float criticalMultiplier = 0;
			
			if(item!=null){ // confirm if player is wearing a weapon
				weapon = (Weapon) item.getType();
				weaponDamage += weapon.getDamage(item);
				criticalMultiplier = weapon.getCritical();
				if(!weapon.use(player)){
					return false;
				}
			}
			
			damage = baseDamage + weaponDamage;
			
			for(Skill skill: ((Player)attacker).getSkills().keySet()){
				if (Modifier.class.isInstance(skill)){
					Modifier modifier = (Modifier)skill; 
					if(modifier.getAffectedSkills().contains(this)){
						if(modifier.getCondition(attacker)){
							if(modifier.getValueType()==Modifier.ValueType.DAMAGE){
								
								switch(modifier.getModifierType()){
									
									case MULTIPLICATIVE:
										damage *= modifier.getModifier(attacker);
										break;
									case ADDITIVE:
										damage += modifier.getModifier(attacker);
										break;
								}
							}
						}
					}						
				}
			}
			
			damage += (long)(damage*criticalMultiplier);
			
			player.setDmgType(criticalMultiplier > 0 ? 1 : 0);
			
			synchronized(victims){
				for(LivingObject victim : victims){
					victim.getsAttacked(player, (int)damage, true);
					player.getClient().sendPacket(Type.ATTACK, player,victim,player.getDmgType());
					//player.getInterested().sendPacket(Type.ATTACK, player, victim, criticalMultiplier > 0 ? 1 : 0);
					
					return true;
				}
			}
		}
		return false;
	}
	
	@Override 
	public void effect(LivingObject source, LivingObject target, int castStep){	
		source.getInterested().sendPacket(Type.ATTACK, source, target, source.getDmgType());
	}
	
	@Override
	public int getMaxLevel() {

		return 0;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {

		return 0;
	}

	@Override
	public int getEffectModifier() {
		return 0;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
}
