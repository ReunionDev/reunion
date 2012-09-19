package org.reunionemu.jreunion.server.database.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.server.database.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/**/*-context.xml", 
	"classpath*:/META-INF/spring/**/*-context-test.xml"})
@ActiveProfiles("test")
public class AccountTest {

	@Autowired 
	AccountRepository accountRespository;
	
	@Test
	public void test() {
		Assert.assertNotNull(accountRespository);
		accountRespository.deleteAll();
		Account account = new Account();
		account.email = "test@example.com";
		accountRespository.save(account);
		Assert.assertNotNull(accountRespository.findByEmail("test@example.com"));
	}

}
