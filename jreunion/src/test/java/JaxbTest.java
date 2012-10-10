

import javax.xml.bind.JAXBContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.generated.Quests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
@ActiveProfiles("test")
public class JaxbTest {
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void test() throws Exception {
		
		Resource resource = context.getResource("file:data/xml/quests.xml");
		
		JAXBContext context = JAXBContext.newInstance(Quests.class);
		
		Quests quests = (Quests) context.createUnmarshaller().unmarshal(resource.getInputStream());
		
		System.out.println(quests);
		
	}

}
