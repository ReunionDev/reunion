package org.reunionemu.jreunion.dao;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.QuestImpl;
@XmlRootElement(name="quests")
public class QuestListImpl extends LinkedList<Quest> implements QuestList {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="quest",type=QuestImpl.class)
	List<Quest> getQuests(){
		
		return this;
	}
	
	
}
