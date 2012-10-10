package org.reunionemu.jreunion.server.database.dao;

import java.util.List;

import org.reunionemu.jreunion.server.database.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountDao<A extends Account> extends CrudRepository<A, Long> {
	
	List<A> findByEmail(String email);
		
}
