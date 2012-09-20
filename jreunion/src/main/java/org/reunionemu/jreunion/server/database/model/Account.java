package org.reunionemu.jreunion.server.database.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@Entity
@Table(name="accounts",
uniqueConstraints=@UniqueConstraint(columnNames = { "id" }))
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
    Long id;
	
	@Column(nullable=false, length=28)
	String username;
	
	@Column(nullable=false, length=28)
	String password;
	
	@Column(nullable=false, name="realname")
	String name;
	
	@Column(nullable=false)
	String email;

	int level;
	
}
