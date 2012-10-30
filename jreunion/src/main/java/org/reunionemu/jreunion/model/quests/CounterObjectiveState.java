package org.reunionemu.jreunion.model.quests;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.jpa.ObjectiveStateImpl;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;


/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */

public class CounterObjectiveState extends ObjectiveStateImpl implements ObjectiveState {
	
	int left;
	public CounterObjectiveState(QuestState questState, Objective objective, int start) {
		super(questState, objective);
		this.left = start;
	}
	
	public CounterObjectiveState(){
		
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
