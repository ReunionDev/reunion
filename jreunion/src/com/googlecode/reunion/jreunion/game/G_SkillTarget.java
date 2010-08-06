package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface G_SkillTarget {

	public void levelUpSkill(G_Skill skill);

	// public void useSkill(G_Skill skill);
	public void useSkill(G_LivingObject livingObject, int skillId);
}