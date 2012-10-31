package org.reunionemu.jreunion.dao;

import java.util.List;

import org.reunionemu.jreunion.model.MemoryWarpSlot;
import org.springframework.data.repository.CrudRepository;

public interface MemoryWarpSlotDao<M extends MemoryWarpSlot, ID extends MemoryWarpSlot.MemoryWarpSlotId> extends CrudRepository<M, ID > {
	
	List<M> findByIdPlayerId(long playerId);
		
}
