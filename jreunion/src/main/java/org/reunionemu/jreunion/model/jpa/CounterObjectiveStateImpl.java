package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.quests.CounterObjectiveState;
import org.reunionemu.jreunion.model.quests.Objective;

@Entity
@Table(name="counterobjectivestate")
public class CounterObjectiveStateImpl extends CounterObjectiveState{

	public CounterObjectiveStateImpl(QuestState questState, Objective objective, int start) {
		super(questState, objective, start);
	}
	
	
	@Column
	@Override
	public int getCounter() {
		return super.getCounter();
	}
	
	@Override
	protected void setCounter(int counter) {
		super.setCounter(counter);
	}

}
