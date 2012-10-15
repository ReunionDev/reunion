package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.reunionemu.jreunion.model.quests.Objective;

@Entity
@Table(name="testcounterobjectivestate")
public class CounterObjectiveState extends ObjectiveState {

	@Column
	Integer counter;
	
	public CounterObjectiveState(QuestState state, Objective objective, Integer start){
		super(state, objective);
		counter = start;
	}
	
	public CounterObjectiveState(){}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}
}
