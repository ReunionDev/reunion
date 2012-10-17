package org.reunionemu.jreunion.dao;


import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;

import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Lazy(true)
public class QuestDaoImpl implements QuestDao {

	private QuestList quests;
	
	@Autowired
	ApplicationContext context;
		
	@Value("${quest.resource.location}")
	Resource resource;
	
	@PostConstruct
	public void init() throws Exception{
		
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
		throw new IllegalStateException("Quest with id: "+id+" not found");
	}

	@Override
	public Quest getRandomQuest(Player player) {
		 
			if (player == null){
				throw new IllegalArgumentException("player");
			}
						
			if(quests.isEmpty()){
				throw new IllegalStateException("No quests loaded in QuestDao");
			}
			
			List<Integer> attempts = new LinkedList<Integer>();
			
			while(true){
				if(attempts.size()>=quests.size()){
					return null;
				}
				int index = Server.getRand().nextInt(quests.size());
				if(attempts.contains(index)){
					continue;
				}
				Quest quest = quests.get(index);
				if(quest.isAllowed(player)){
					return quest;
				}
				
				attempts.add(index);
			}
	}
	
}
