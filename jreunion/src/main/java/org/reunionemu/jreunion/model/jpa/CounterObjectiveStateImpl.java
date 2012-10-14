package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.model.quests.objectives.CounterObjectiveState;

@Entity
@Table(name="counterobjectives",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class CounterObjectiveStateImpl extends CounterObjectiveState implements ObjectiveStateSuper {


	public CounterObjectiveStateImpl(int start) {
		super(start);
	}

	private Long id;
	
	
	
	@Column(name="counter")
	@Override
	public int getCounter() {
		return super.getCounter();
	}
	@Override
	protected void setCounter(int counter) {
		super.setCounter(counter);
	}
	@Override
	
	@Id
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}	
	
}

