package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.DatabaseUtils;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class EntityManager {
	private java.util.List<Entity> entityList = new Vector<Entity>();

	private static EntityManager _instance;

	public static synchronized EntityManager getEntityManager() {
		if (_instance == null) {
			try {
				_instance = new EntityManager();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return _instance;
	}

	private List<Integer> entityIdPool = new Vector<Integer>();

	private EntityManager() throws Exception {
		if (!DatabaseUtils.getInstance().checkDatabase()) {
			throw new Exception();
		}
		//entityIdPool = DatabaseUtils.getInstance().getUsedIds();
	}

	public void addEntity(Entity ent) {
		entityList.add(ent);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void createEntity(Entity entity) {
		if (entity == null) {
			return;
		}
		int freeid = 0;
		boolean found = false;
		while (!found) {
			int check = freeid;
			Iterator<Entity> iter1 = EntityManager.getEntityManager()
					.getEntityListIterator();
			while (iter1.hasNext()) {
				if (freeid == iter1.next().getEntityId()) {
					freeid++;
				} else {
					Iterator<Integer> iter2 = entityIdPool.iterator();
					while (iter2.hasNext()) {
						if (freeid == iter2.next().intValue()) {
							freeid++;
						}
					}
				}
			}
			if (check == freeid) {
				break;
			}

		}

		try {
			entity.setEntityId(freeid);
			entityIdPool.add(entity.getEntityId());
			addEntity(entity);
		} catch (Exception e) {
			return;
		}

		return;
	}

	public void destroyEntity(Entity entity) {
		entityList.remove(entity);

		Iterator<Integer> iter = entityIdPool.iterator();
		while (iter.hasNext()) {
			Integer i = iter.next();
			if (i.intValue() == entity.getEntityId()) {
				entityIdPool.remove(i);
				return;
			}
		}

	}

	public Entity getEnt(int entityid) {
		Iterator<Entity> iter = getEntityListIterator();
		while (iter.hasNext()) {
			Entity entity = iter.next();
			if (entity.getEntityId() == entityid) {
				return entity;
			}
		}
		return null;

	}

	public Iterator<Entity> getEntityListIterator() {
		return entityList.iterator();
	}

	public void loadEntity(Entity entity, int uniqueid) {
		if (entity == null) {
			return;
		}
		if (entity.getEntityId() != -1) {
			return;
		}

		try {
			entity.setEntityId(uniqueid);
			addEntity(entity);
		} catch (Exception e1) {
		}

	}

	public void removeEntity(Entity entity) {
		entityList.remove(entity);

	}

}