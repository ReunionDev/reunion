package com.googlecode.reunion.jreunion.server.database;

import java.sql.Connection;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class DatabaseAction {
	
	Connection connection;
	
	public DatabaseAction(Connection connection)
	{	
		this.connection = connection;
	}
	
	public abstract void perform() throws Exception;
	
}
