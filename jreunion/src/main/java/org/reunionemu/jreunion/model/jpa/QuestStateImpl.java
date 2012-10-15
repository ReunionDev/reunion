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
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="queststates",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class QuestStateImpl extends QuestState {
	@Autowired
	private QuestDao questDao;
	private Long id;	
	
	private List<ObjectiveState> objectives = new LinkedList<ObjectiveState>();	
	
	protected QuestStateImpl(){}
	
	public QuestStateImpl(Quest quest) {
		super(quest);
		for(Objective objective: quest.getObjectives()){
			getObjectives().add(objective.createObjectiveState(this));
		}
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	
	@Transient
	public Integer getObjectiveId(Objective objective) {
		int count = 0;
		for(Objective obj: getQuest().getObjectives())
		{
			if(obj.equals(objective)){
				return count;
			}
			count++;
		}
		throw new RuntimeException("Objective not found in quest");
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER, mappedBy = "questState", targetEntity=ObjectiveStateImpl.class)
	public List<ObjectiveState> getObjectives() {
		return objectives;
	}

	@Override
	public ObjectiveState getObjectiveState(Objective objective) {
		for(ObjectiveState state: getObjectives()){
			if(state.getObjective()==objective){
				return state;
			}
		}
		throw new RuntimeException("Objective not found");
	}

	@Column(name="quest_id")
	public Integer getQuestId(){
		return getQuest().getId();
	}
	
	protected void setId(Long id) {
		this.id = id;
	}

	protected void setObjectives(List<ObjectiveState> objectives) {
		this.objectives = objectives;
	}

	public void setQuestId(int questId){
		setQuest(questDao.findById(questId));
	}
	
	
}

