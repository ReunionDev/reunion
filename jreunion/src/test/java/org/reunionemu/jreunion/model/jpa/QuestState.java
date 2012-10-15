package org.reunionemu.jreunion.model.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.quests.Objective;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Table(name="testqueststate")
@Entity
@Configurable(preConstruction=true, autowire=Autowire.BY_TYPE)
public class QuestState {
	
	
	@Autowired
	public QuestDao questDao;	
	
	protected QuestState(){
		
	}
	
	public QuestState(Quest quest){
		this.quest = quest;
	}
	
	public Long id;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	
	@Transient
	Quest quest;
	
	public List<ObjectiveState> objectives = new LinkedList<ObjectiveState>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, mappedBy = "questState")
	public List<ObjectiveState> getObjectives() {
		return objectives;
	}

	protected void setObjectives(List<ObjectiveState> objectives) {
		this.objectives = objectives;
	}

	@Column(name="quest_id")
	public Integer getQuestId(){
		return quest.getId();
	}
	
	public void setQuestId(int questId){
		quest = questDao.findById(questId);
	}
	
	@Transient
	public Quest getQuest() {
		return quest;
	}

	@Transient
	public Integer getObjectiveId(Objective objective) {
		int count = 0;
		for(Objective obj: quest.getObjectives())
		{
			if(obj.equals(objective)){
				return count;
			}
			count++;
		}
		throw new RuntimeException("Objective not found in quest");
	}
	
	
}
