package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public interface SkillTarget {

	// public void useSkill(G_Skill skill);
	public void useSkill(LivingObject livingObject, int skillId);
}