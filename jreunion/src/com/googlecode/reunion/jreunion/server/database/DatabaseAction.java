package com.googlecode.reunion.jreunion.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseAction {
	
	Connection connection;
	
	public DatabaseAction(Connection connection)
	{	
		this.connection = connection;
	}
	
	public abstract void perform() throws Exception;
}
