package org.reunionemu.jreunion.model.quests.objectives;

public class CounterObjectiveState implements ObjectiveState {

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
