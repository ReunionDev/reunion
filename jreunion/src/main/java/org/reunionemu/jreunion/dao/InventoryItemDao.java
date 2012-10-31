package org.reunionemu.jreunion.dao;

import java.util.List;

import org.reunionemu.jreunion.game.InventoryItem;
import org.springframework.data.repository.CrudRepository;

public interface InventoryItemDao<I extends InventoryItem> extends CrudRepository<I, Long> {
	
	List<I> findByPlayerId(int playerId);
	
	I findByItemId(long itemId);
		
}
