package org.reunionemu.jreunion.model.jpa;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.model.quests.Objective;
import org.reunionemu.jreunion.model.quests.objectives.ObjectiveState;

@Entity
@Table(name="queststates",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class QuestStateImpl extends QuestState {
	public QuestStateImpl(Quest quest) {
		super(quest);
	}

	@Id @GeneratedValue
	private Long id;
	
	@OneToMany(mappedBy="objectives")
	@MapKey(name="type")
	@Override
	public Map<Objective, ObjectiveState> getProgression() {
		return super.getProgression();
	}
	
	
}

