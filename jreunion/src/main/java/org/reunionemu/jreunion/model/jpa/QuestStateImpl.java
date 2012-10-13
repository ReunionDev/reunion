package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.model.Quest;

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
	
}

