package org.reunionemu.jreunion.dao;

import java.util.List;

import org.reunionemu.jreunion.game.RoamingItem;
import org.springframework.data.repository.CrudRepository;

public interface RoamingItemDao<RI extends RoamingItem> extends CrudRepository<RI, Long> {
	
	List<RI> findByMapId(int mapId);
		
}
