package com.googlecode.reunion.jreunion.game.skills.human;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.game.Castable;
import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.HumanPlayer;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
import com.googlecode.reunion.jreunion.server.Tools;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

public class ElectricShield extends Skill implements Castable, Effectable {
	
	private int effectModifier = 0;
	
	private Timer timer = null;

	public ElectricShield(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return 14+skillLevel;
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
		 * level 25 = 30%
		 */
		
		return 0.39f / (getMaxLevel() - 1);
		
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
		/* level 1 = 5%
		 * level 2 = 
		 * ...
		 * level 25 = 50%
		 */
		
		return 0.45f / (getMaxLevel() - 1);
		
	}
	
	public float getDefenceBonusModifier(Player player){
		
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (0.05f+((level-1) * getDamageAbsorbModifier()));			
		}	
		
		return modifier;
	}
	
	public float getElectricModifier(){
		/* electric spent:
		 * level 1 = 10
		 * level 2 = 12
		 * level 3 = 14
		 * ...
		 * level 25 = 60
		 */
		return 50f/(getMaxLevel()-1);
	}
	
	float getElectricModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (10 + ((level-1) * getElectricModifier()));			
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
	
	float getDurationModifier(Player player){
		float modifier = 0;
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
	
	float getAccumulatedTimeModifier(Player player){
		float modifier = 0;
		int level = player.getSkillLevel(this);
		
		if(level>0){
			modifier += (110 + ((level-1) * getAccumulatedTimeModifier()));			
			}	
		
		return modifier;
	}
	
	@Override
	//TODO: handle with DefenceBonus and DamageAbsorb
	public boolean cast(LivingObject caster, List<LivingObject> victims) {
		
		if(caster instanceof HumanPlayer){
			final Player player = (Player) caster;
			int newElectric = player.getElectricity() - (int) getElectricModifier(player);
			
			if(getEffectModifier() == (int)getAccumulatedTimeModifier(player)){
				player.getClient().sendPacket(Type.SAY, "ElectricShield skill acumulated time, already at maximum.");
				return false;
			}
			
			if(getEffectModifier() == 0)
				player.getClient().sendPacket(Type.SAY, "ElectricShield skill activated.");
			
			player.setElectricity(newElectric);
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
				    		player.getClient().sendPacket(Type.SAY, "ElectricShield skill will end soon...");
				    		player.getClient().sendPacket(Type.SKILL, player, skill);
				    	}
				      } else {
				    	  player.getClient().sendPacket(Type.SAY, "ElectricShield skill deqactivated.");
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
