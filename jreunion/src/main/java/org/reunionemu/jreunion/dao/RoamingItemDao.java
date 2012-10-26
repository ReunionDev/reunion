package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.RoamingItem;
import org.springframework.data.repository.CrudRepository;

public interface RoamingItemDao<RI extends RoamingItem> extends CrudRepository<RI, Long> {
	
		
}
