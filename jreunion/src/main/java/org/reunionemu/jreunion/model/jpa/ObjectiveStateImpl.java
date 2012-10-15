package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction=true)
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name="objectivestate")
public abstract class ObjectiveStateImpl implements ObjectiveState {
	
	Long id;
	
	QuestState questState;
	
	Objective objective;
	
	private Integer objectiveId;

	@Autowired
	QuestDao questDao;

	public ObjectiveStateImpl(){}

	public ObjectiveStateImpl(QuestState questState, Objective objective) {
		super();
		this.questState = questState;
		this.objective = objective;
	}

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}


	@Transient
	public Objective getObjective() {
		if(objective==null && objectiveId!=null){
			objective = questState.getQuest().getObjectives().get(objectiveId);
		}
		return objective;
	}
	
	@Column(name="objective_id")
	protected Integer getObjectiveId() {
		return questState.getObjectiveId(objective);
	}
	
	@ManyToOne(targetEntity=QuestStateImpl.class)
	@JoinColumn(name="queststate_id")
	public QuestState getQuestState() {
		return questState;
	}
	
	protected void setId(Long id) {
		this.id = id;
	}
	
	protected void setObjective(Objective objective) {
		this.objective = objective;
	}

	
	
	protected void setObjectiveId(Integer objectiveId) {
		this.objectiveId = objectiveId;
		
	}
	
	
	protected void setQuestState(QuestState questState) {
		this.questState = questState;
	}
	  
	
	
}
