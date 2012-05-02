import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.integration.samples.tcpclientserver.SimpleGateway;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/spring/context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class IntegrationTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Autowired
	SimpleGateway gw;

	@Test
	public void test() throws Exception {
		System.out.println("starting test");
		gw.send("test");
		gw.send("test");
		gw.send("test");
		
		gw.send("test");
		gw.send("test");
		gw.send("test");
		gw.send("test");
		gw.send("test");
		
		
	}

}
