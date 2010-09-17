package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.DatabaseUtils;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_EntityManager {
	private java.util.List<G_Entity> entityList = new Vector<G_Entity>();

	private static G_EntityManager ref;

	public static synchronized G_EntityManager getEntityManager() {
		if (ref == null) {
			try {
				ref = new G_EntityManager();
			} catch (Exception e) {
				return null;
			}
		}
		return ref;
	}

	private List<Integer> entityIdPool = new Vector<Integer>();

	private G_EntityManager() throws Exception {
		if (!DatabaseUtils.getInstance().checkDatabase()) {
			throw new Exception();
		}
		entityIdPool = DatabaseUtils.getInstance().getUsedIds();
	}

	public void addEntity(G_Entity ent) {
		entityList.add(ent);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void createEntity(G_Entity entity) {
		if (entity == null) {
			return;
		}
		int freeid = 0;
		boolean found = false;
		while (!found) {
			int check = freeid;
			Iterator<G_Entity> iter1 = G_EntityManager.getEntityManager()
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

	public void destroyEntity(G_Entity entity) {
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

	public G_Entity getEnt(int entityid) {
		Iterator<G_Entity> iter = getEntityListIterator();
		while (iter.hasNext()) {
			G_Entity entity = iter.next();
			if (entity.getEntityId() == entityid) {
				return entity;
			}
		}
		return null;

	}

	public Iterator<G_Entity> getEntityListIterator() {
		return entityList.iterator();
	}

	public void loadEntity(G_Entity entity, int uniqueid) {
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

	public void removeEntity(G_Entity entity) {
		entityList.remove(entity);

	}

}