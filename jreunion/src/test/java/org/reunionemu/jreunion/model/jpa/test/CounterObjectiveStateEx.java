package org.reunionemu.jreunion.model.jpa.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.reunionemu.jreunion.model.quests.Objective;

@Entity
@Table(name="testcounterobjectivestate")
public class CounterObjectiveStateEx extends ObjectiveState {

	@Column
	Integer counter;
	
	public CounterObjectiveStateEx(QuestState state, Objective objective, Integer start){
		super(state, objective);
		counter = start;
	}
	
	public CounterObjectiveStateEx(){}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}
}
