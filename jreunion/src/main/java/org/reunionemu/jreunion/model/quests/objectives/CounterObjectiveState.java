package org.reunionemu.jreunion.model.quests.objectives;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CounterObjectiveState implements ObjectiveState {
	
	//@Id @GeneratedValue
	//Long id;
	
	int start;
	int left;
	public CounterObjectiveState(int start){
		this.start = start;
		this.left = start;
	}
	
	public void decrease(){
		left--;
	}
	
	public int getCounter(){
		return left;
	}
	
	@Override
	public boolean isComplete() {
		return left<=0;
	}

}
