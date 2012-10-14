package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public interface ObjectiveStateSuper {
	@Column
	@Id @GeneratedValue
	public Long getId();	
	
	void setId(Long id);

}
