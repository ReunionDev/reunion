package org.reunionemu.jreunion.dao;

import org.reunionemu.jreunion.game.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemDao<A extends Item<?>> extends CrudRepository<A, Long> {
	
		
}
