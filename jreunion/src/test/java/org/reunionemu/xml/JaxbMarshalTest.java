package org.reunionemu.xml;


import java.io.*;

import javax.xml.bind.*;

import org.junit.Test;
import org.reunionemu.jreunion.dao.*;
import org.reunionemu.jreunion.model.*;
import org.reunionemu.jreunion.model.quests.rewards.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class JaxbMarshalTest {
	
	Logger logger = LoggerFactory.getLogger(JaxbMarshalTest.class);
	
	@Autowired
	ApplicationContext context;
	
	public QuestListImpl createQuestList(){
		QuestListImpl list = new QuestListImpl();
		
		Quest quest = new QuestImpl(){
			{
				id = 1;
				name = "test quest";
				description = "this is a test quest";				
			}			
		};
		

		{
			ExperienceReward reward = new ExperienceRewardImpl(){
				{
					experience = 10;					
				}
				
			};
			quest.getRewards().add(reward);

		}
		{
			LimeReward reward = new LimeRewardImpl(){
				{
					lime = 10;
					
				}
				
			};
			quest.getRewards().add(reward);
		}
		{
			ItemReward reward = new ItemRewardImpl(){
				{
					amount = 10;
					type = 11;
					
				}
				
			};
			quest.getRewards().add(reward);
		}
		{
			ItemReward reward = new ItemRewardImpl(){
				{
					type = 12;
					
				}
				
			};
			quest.getRewards().add(reward);
		}
		
		list.add(quest);
		
		return list;
	}
	
	@Test
	public void test() throws Exception {
		
		
		QuestListImpl list = createQuestList();
		
		JAXBContext context = JAXBContext.newInstance(QuestListImpl.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//marshaller.setProperty(Marshaller, true);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		marshaller.marshal(list, baos);
		byte [] xml = baos.toByteArray();
	
		logger.debug(new String(xml));
		
		QuestList quests = (QuestList)unmarshaller.unmarshal(new ByteArrayInputStream(xml));
		for(Quest quest: quests){
			logger.debug(quest.getName());
		
		}
		
	}

}
