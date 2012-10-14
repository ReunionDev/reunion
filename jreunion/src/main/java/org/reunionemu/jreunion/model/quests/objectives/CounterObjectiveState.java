package org.reunionemu.jreunion.model.quests.objectives;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */

public class CounterObjectiveState implements ObjectiveState {
	
	int left;
	public CounterObjectiveState(int start){
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

	protected void setCounter(int counter) {
		this.left = counter;
	}

}
