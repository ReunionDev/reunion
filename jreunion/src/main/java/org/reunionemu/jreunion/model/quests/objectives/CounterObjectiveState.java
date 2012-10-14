package org.reunionemu.jreunion.model.quests.objectives;

import javax.persistence.Entity;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
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
