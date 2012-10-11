package org.reunionemu.jreunion.dao;

import java.util.List;

import org.reunionemu.jreunion.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountDao<A extends Account> extends CrudRepository<A, Long> {
	
	List<A> findByEmail(String email);
	A findByUsernameAndPassword(String username, String password);
		
}
