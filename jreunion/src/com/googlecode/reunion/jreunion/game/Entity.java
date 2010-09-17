package com.googlecode.reunion.jreunion.game;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Entity {
	private int e_id;

	public Entity() {
		e_id = -1;

	}

	public int getEntityId() {
		return e_id;
	}

	public void removeEntity() {
		EntityManager.getEntityManager().removeEntity(this);
	}

	public void setEntityId(int id) throws Exception {
		Exception e = new Exception();
		if (!(Class
				.forName("com.googlecode.reunion.jreunion.game.G_EntityManager") == Class
				.forName(e.getStackTrace()[1].getClassName()))) {
			throw new Exception("Cannot set the entity id no permission");
		}

		e_id = id;
	}

}