package org.reunionemu.jreunion.server.database.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames = { "id" }))
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
    Long id;
	
	String username;
	
	String password;
	
	String name;
	
	String email;
	
	int level;
	
}
