package org.reunionemu.jreunion.server.database.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.reunionemu.jreunion.server.database.model.Account;


@Entity
@Table(name="accounts",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" }),
		@UniqueConstraint(columnNames = { "username" })
})
public class AccountImpl implements Account{
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue
    Long id;
	
	@Column(nullable=false, length=28)
	String username;
	
	@Column(nullable=false, length=28)
	String password;
	
	@Column(nullable=false)
	String name;
	
	@Column(nullable=false)
	String email;

	int level;

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
		
	}
	
}
