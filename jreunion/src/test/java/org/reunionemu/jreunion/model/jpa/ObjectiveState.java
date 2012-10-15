package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.model.quests.Objective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction=true)
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name="testobjectivestate")
public abstract class ObjectiveState {
	
	Long id;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}


	protected void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public QuestState getQuestState() {
		return questState;
	}


	protected void setQuestState(QuestState questState) {
		this.questState = questState;
	}

	@Transient
	public Objective getObjective() {
		return objective;
	}


	protected void setObjective(Objective objective) {
		this.objective = objective;
	}
	
	@Autowired
	QuestDao  questDao;
	
	@Column(name="objective_id")
	public Integer getObjectiveId() {
		return questState.getObjectiveId(objective);
	}
	
	protected void setObjectiveId(Integer objectiveId) {
		objective = questState.getQuest().getObjectives().get(objectiveId);
	}

	QuestState questState;
	
	
	Objective objective;
	
	public ObjectiveState(){}
	
	
	public ObjectiveState(QuestState questState, Objective objective) {
		super();
		this.questState = questState;
	}
	  
	
		
}
