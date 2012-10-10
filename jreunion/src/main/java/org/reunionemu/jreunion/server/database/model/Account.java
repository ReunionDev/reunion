package org.reunionemu.jreunion.server.database.model;

import java.io.Serializable;

public interface Account extends Serializable {
	
	public String getEmail();
	void setEmail(String email);
	String getUsername();
	void setUsername(String username);
	String getPassword();
	void setPassword(String password);
	String getName();
	void setName(String name);
	int getLevel();
	void setLevel(int level);
	Long getId();

}
