package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="testcounterobjectivestate")
public class CounterObjectiveState extends ObjectiveState {

	@Column
	Integer counter;
	
	public CounterObjectiveState(QuestState state, Integer start){
		super(state);
		counter = start;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(Integer counter) {
		this.counter = counter;
	}
}
