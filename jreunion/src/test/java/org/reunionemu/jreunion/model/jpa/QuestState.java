package org.reunionemu.jreunion.model.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.QuestImpl;

@Table(name="testqueststate")
@Entity
public class QuestState {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	transient Quest quest;
	
    @OneToMany()
    @JoinColumn(name="queststate_id")
	public List<ObjectiveState> objs = new LinkedList<ObjectiveState>();
	
	@Column(name="quest_id", nullable=true)
	public Integer getQuestId(){
		return quest.getId();
	}
	
	public void setQuestId(int questId){
		quest = loadQuest(questId);
	}
		
	public static Quest loadQuest(int questId){
		return new QuestImpl();
	}
	
	
}
