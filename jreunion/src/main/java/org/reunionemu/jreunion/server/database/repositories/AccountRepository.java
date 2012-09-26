package org.reunionemu.jreunion.server.database.repositories;

import java.util.List;

import org.reunionemu.jreunion.server.database.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
	
	List<Account> findByEmail(String email);
	
		
}
