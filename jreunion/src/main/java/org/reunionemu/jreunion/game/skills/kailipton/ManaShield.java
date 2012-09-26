package org.reunionemu.jreunion.game.skills.kailipton;

import java.util.Timer;
import java.util.TimerTask;

import org.reunionemu.jreunion.game.Castable;
import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.KailiptonPlayer;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.server.SkillManager;
import org.reunionemu.jreunion.server.Tools;
import org.reunionemu.jreunion.server.PacketFactory.Type;


public class ManaShield extends Skill implements Castable, Effectable {

	private int effectModifier = 0;
	
	private Timer timer = null;
	
	public ManaShield(SkillManager skillManager,int id) {
		super(skillManager,id);
	}
	
	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 14 + skillLevel;
	}
	
	@Override
	public int getAffectedTargets() {
		return 1;
	}
	
	public int getEffectModifier(){
		return effectModifier;
	}
	
	public void setEffectModifier(int effectModifier){
		this.effectModifier = effectModifier;
	}
	
	public float getDamageAbsorbModifier(){
		/* level 1 = 1%
		 * level 2 = 
		 * ...
		 * level 25 = 20%
		 */
		
		return 0.19f / (getMaxLevel() - 1);
		
	}
	
	public float getDamageAbsorbModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.01f+((level-1) * getDamageAbsorbModifier()));			
		}	
		
		return modifier;
	}
	
	public float getDefenceBonusModifier(){
		/* level 1 = 10%
		 * level 2 = 
		 * ...
		 * level 25 = 70%
		 */
		
		return 0.6f / (getMaxLevel() - 1);
		
	}
	
	public float getDefenceBonusModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.1f+((level-1) * getDamageAbsorbModifier()));			
		}	
		
		return modifier;
	}
	
	public float getManaModifier(){
		/* mana spent:
		 * level 1 = 10
		 * level 2 = 12
		 * level 3 = 14
		 * ...
		 * level 25 = 60
		 */
		return 50f/(getMaxLevel()-1);
	}
	
	public long getManaModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getManaModifier()));			
			}	
		
		return modifier;
	}
	
	public float getDurationModifier(){
		/* mana spent:
		 * level 1 = 6
		 * level 2 = 7
		 * level 3 = 8
		 * ...
		 * level 25 = 30
		 */
		return 24f/(getMaxLevel()-1);
	}
	
	public long getDurationModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (6 + ((level-1) * getDurationModifier()));			
			}	
		
		return modifier;
	}
	
	public float getAccumulatedTimeModifier(){
		/* mana spent:
		 * level 1 = 110
		 * level 2 = 120
		 * level 3 = 130
		 * ...
		 * level 25 = 350
		 */
		return 240f/(getMaxLevel()-1);
	}
	
	public long getAccumulatedTimeModifier(Player player){
		long modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (110 + ((level-1) * getAccumulatedTimeModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	//TODO: handle with DefenceBonus and DamageAbsorb
	public boolean cast(LivingObject caster, LivingObject victim, String[] arguments) {
		
		if(caster instanceof KailiptonPlayer){
			final Player player = (Player) caster;
			long newMana = player.getMana() - getManaModifier(player);
			
			if(getEffectModifier() == (int)getAccumulatedTimeModifier(player)){
				player.getClient().sendPacket(Type.SAY, "ManaShield skill acumulated time, already at maximum.");
				return false;
			}
			
			if(getEffectModifier() == 0)
				player.getClient().sendPacket(Type.SAY, "ManaShield skill activated.");
			
			player.setMana(newMana);
			setEffectModifier(Tools.between(getEffectModifier()+(int)getDurationModifier(player), 0, (int)getAccumulatedTimeModifier(player)));
			
			if(timer != null)
				timer.cancel();
			
			timer = new Timer();
			final Skill skill = (Skill)this;
			
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
				      if (getEffectModifier() > 0) {
				    	setEffectModifier(getEffectModifier() - 1);
				    	if(getEffectModifier() == 20){
				    		player.getClient().sendPacket(Type.SAY, "ManaShield skill will end soon...");
				    		player.getClient().sendPacket(Type.SKILL, player, skill);
				    	}
				      } else {
				    	  player.getClient().sendPacket(Type.SAY, "ManaShield skill deactivated.");
				    	  player.getClient().sendPacket(Type.SKILL, player, skill);
				    	  timer.cancel();
				      }
				    }
			}, 1, 1 * 1000);
			return true;
		}
			
		return false;
	}
}