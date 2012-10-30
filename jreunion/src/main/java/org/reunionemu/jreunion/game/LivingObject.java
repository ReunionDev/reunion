package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.model.Quest;

import org.reunionemu.jreunion.server.Tools;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public abstract class LivingObject extends WorldObject {

	private LivingObject target;
	
	private Position targetPosition;
	
	private String name;
	
	private long hp;

	private long maxHp;

	private int level;
	
	private int dmgType;	// 0-normal; 1-critical; 2-demolition; 3-super critical; 4-Explosion;

	public Position getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Position targetPosition) {
		this.targetPosition = targetPosition;
	}
	
	public int getPercentageHp() {
		double percentageHp = this.getHp() * 100 / this.getMaxHp();
		return (int) ((percentageHp < 1 && percentageHp > 0) ? 1 : percentageHp);		
	}
	
	public void walk(Position position, boolean isRuning) {

		//setIsRunning(isRunning);
		synchronized(this) {
			setPosition(position);
			setTargetPosition(position.clone());			
		}
		getInterested().sendPacket(Type.WALK, this, position);
				
	}
	
	public int getDmgType() {
		return dmgType;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	
	//players: 0-walking 1-running ; npc: 0-stoped 1-moving
	private boolean running;

	public void setIsRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	public LivingObject() {
		super();
	}

	public long getHp() {
		return hp;
	}

	public int getLevel() {
		return level;
	}

	public long getMaxHp() {
		return maxHp;
	}

	public LivingObject getTarget() {
		return target;
	}

	public void loadFromReference(int id) {

	}

	public void setHp(long hp) {
		this.hp = Tools.between(hp, 0l, getMaxHp());		
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHp(long maxHp) {
		this.maxHp = maxHp;
	}

	public void setTarget(LivingObject target) {
		this.target = target;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String livingObjectName){
		this.name = livingObjectName;
	}
	
	// 1-short range melee; 2-magic; 3-summon; 4-long range melee;
	public int getLastAttackType(){
		if (this instanceof Player) {
			Player player = (Player) this;

			// as Kailipton can use magic without a weapon, we will consider all attacks as Magic attack. 
			if (player.getRace() == Race.KAILIPTON) {
				return 2;
			} else {
				Item<?> weapon = player.getEquipment().getMainHand();
				if (weapon == null) {
					return 1;
				} else {
					if (weapon.getType() instanceof MeleeWeapon) {
						return 1;
					} else if (weapon.getType() instanceof MagicWeapon) {
						return 2;
					} else if (weapon.getType() instanceof SummonWeapon) {
						return 3;
					} else if (weapon.getType() instanceof RangedWeapon) {
						return 4;
					}
				}
			}
		} else if(this instanceof Pet){
			return 1;
		} else if(this instanceof Npc){
			if(((Npc<?>)this).getType() instanceof Mob){
				Mob mob = (Mob)((Npc<?>)this).getType();
				return mob.getAttackType();
			}
		}
		
		return 0;
	}
	
	public void getsAttacked(Player player, long damage, boolean addAttack){
		
		Npc<?> npc = null;
		Mob mob = null;
		
		if(this instanceof Npc){
			npc = (Npc<?>)this;
			if(npc.getType() instanceof Mob){
				mob = (Mob) npc.getType();
			}
		}
		
		if(addAttack){
			player.addAttack(damage);
		}
		
		/*
		  
		// TODO: reimplement using the new quest system
		 
		
		//Cursed quest Boss packet
		if(mob != null){
			QuestState questState = player.getQuestState();
			
			if(questState != null){
				Quest quest = questState.getQuest();
				if(quest instanceof ExperienceQuest){
					Objective objective = quest.getObjective(npc.getType().getTypeId());
					if(objective != null){
							if(questState.getProgression(objective.getId()) == (objective.getAmmount()-1)){
								if(!npc.isBoss()){
									player.getClient().sendPacket(Type.QT, "king "+this.getEntityId()+" 1");
									npc.setBoss();
								}
							}
					}
				}
			}
		}*/
		
		if(npc.isMutant()){
			damage = (long)(damage * npc.getMutantResistance(player));
			
			// Damage Calculation for Mutants
			// The Mutant which is for the player race, gets 100% resistance value
			// All other Mutant colors getting 25% resistance value
			
			/*
			float resistance = npc.getMutantResistance(player);
			
			// Value caps to prevent invincible mobs and other problems
			if (resistance > 0.9) { resistance = 0.9f; }
			if (resistance < 0.1) { resistance = 0.1f; }
				
			if (npc.getMutantType() == 1) {
				if (player.getRace() == Player.Race.BULKAN ) {
					damage = (long)(damage * resistance);
				} else if (player.getRace() == Player.Race.HYBRIDER ) {
					damage = (long)(damage * resistance);
				} else if (player.getRace() == Player.Race.PET ) {
					damage = (long)(damage * resistance);
				}
				
			}
			else if (npc.getMutantType() == 2) {
				if (player.getRace() == Player.Race.KAILIPTON) {
					damage = (long)(damage * resistance);
				}
			}
			else if (npc.getMutantType() == 3) {
				if (player.getRace() == Player.Race.AIDIA) {
					damage = (long)(damage * resistance);
				}
			}
			else if (npc.getMutantType() == 4) {
				if (player.getRace() == Player.Race.HUMAN) {
					damage = (long)(damage * resistance);
				}
			} else {
				// 25% of resistant value for non class specific resistance
				resistance = resistance / 4;
				if (resistance < 0.1) { resistance = 0.1f; }		
				damage = (long)(damage * resistance);
			}
			*/
			
		}
		long newHp = getHp() - damage;
		setHp(newHp);
		
		if (this.getPercentageHp() < 1) {
			LoggerFactory.getLogger(LivingObject.class).info("Player "+player+" killed npc "+this);
			if(npc != null){
				npc.kill(player);
			}
		}
		this.getInterested().sendPacket(Type.ATTACK_VITAL, this);
	}

	public static enum AttackType {
		
		NO_ATTACK(0),
		CLOSE_MELEE(1),
		MAGIC(2),
		SUMMON(3),
		RANGE_MELEE(4);
		
		int value;
		AttackType(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
		
		public static AttackType byValue(int attackTypeId){			
			for(AttackType attackType:AttackType.values())
			{
				if(attackType.value()==attackTypeId){					
					return attackType;
				}
			}
			return null;
		}
	}
	
	public static enum DamageType {
		
		NO_DAMAGE(-1),
		NORMAL(0),
		CRITICAL(1),
		DEMOLITION(2),
		FIREBALL(3),
		LIGHTNING(4),
		LIGHTNINGBALL(5),
		STARFLARE(6);
		
		int value;
		DamageType(int value){
			this.value = value;
		}
		
		public int value(){
			return value;
		}
		
		public static DamageType byValue(int damageTypeId){			
			for(DamageType damageType:DamageType.values())
			{
				if(damageType.value()==damageTypeId){					
					return damageType;
				}
			}
			return null;
		}
	}
}