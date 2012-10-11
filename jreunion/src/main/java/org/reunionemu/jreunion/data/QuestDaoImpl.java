package org.reunionemu.jreunion.data;


import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;

import org.reunionemu.jreunion.data.quests.Quest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
@Lazy(true)
public class QuestDaoImpl implements QuestDao {

	private QuestList quests;
	
	@Autowired
	ApplicationContext context;
	
	@Value("${quest.resource.location}")
	String resourceLocation;
	
	@PostConstruct
	public void init() throws Exception{
		Resource resource = context.getResource(resourceLocation);
		
		JAXBContext context = JAXBContext.newInstance(QuestListImpl.class);
		
		quests = (QuestList) context.createUnmarshaller().unmarshal(resource.getInputStream());
		
	}
	
	@Override
	public Quest findById(int id) {
		for(Quest quest: quests){
			if(quest.getId()==id){
				return quest;
			}
		}
		throw new IllegalStateException("Quest with id: "+id+"not found");
	}
	
}
