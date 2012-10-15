package org.reunionemu.jreunion.model.jpa.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.reunionemu.jreunion.model.quests.Objective;


@Entity
@Table(name="testdummyobjectivestate")
public class DummyObjectiveState extends ObjectiveState {

	@Column
	String dummy;
	
	public DummyObjectiveState(QuestState state, Objective objective, String dummy){
		super(state, objective);
		this.dummy = dummy;
	}
	
	public DummyObjectiveState(){}

}
