package org.reunionemu.xml;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.reunionemu.jreunion.dao.QuestList;
import org.reunionemu.jreunion.dao.QuestListImpl;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.QuestImpl;
import org.reunionemu.jreunion.model.quests.rewards.ExperienceReward;
import org.reunionemu.jreunion.model.quests.rewards.ExperienceRewardImpl;
import org.reunionemu.jreunion.model.quests.rewards.ItemReward;
import org.reunionemu.jreunion.model.quests.rewards.ItemRewardImpl;
import org.reunionemu.jreunion.model.quests.rewards.LimeReward;
import org.reunionemu.jreunion.model.quests.rewards.LimeRewardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class JaxbMarshalTest {
	
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
		System.out.println(new String(xml));
		
		QuestList quests = (QuestList)unmarshaller.unmarshal(new ByteArrayInputStream(xml));
		for(Quest quest: quests){
			System.out.println(quest.getName());
		}
		
	}

}
