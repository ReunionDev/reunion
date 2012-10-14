package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity @Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Table(name="testobjectivestate")
public class ObjectiveState {
	
	@Id @GeneratedValue(strategy=GenerationType.TABLE)
	Long id;
	
	@ManyToOne
	QuestState questState;
	public ObjectiveState(){}
	
	
	public ObjectiveState(QuestState questState) {
		super();
		this.questState = questState;
	}
	  
		
}
