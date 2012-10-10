package org.reunionemu.jreunion.server.database.model;

import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.server.database.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
@ActiveProfiles("test")
public class AccountTest {

	@Autowired 
	AccountRepository accountRespository;
	
	@Test
	public void test() {
		Assert.assertNotNull(accountRespository);
		Account account = new Account();
		account.username = "test";
		account.email = "test@example.com";
		account.name = "John Doe";
		account.password = "1234";
		
		accountRespository.save(account);
		
		List<Account> accounts = accountRespository.findByEmail("test@example.com");
		Assert.assertNotNull(accounts);
		Assert.assertThat(accounts.size(), greaterThan(0));
		
		accountRespository.delete(accounts);
		accounts = accountRespository.findByEmail("test@example.com");
		Assert.assertThat(accounts.size(), is(0));		
	}

}
