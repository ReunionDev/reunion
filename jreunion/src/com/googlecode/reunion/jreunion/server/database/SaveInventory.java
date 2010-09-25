package com.googlecode.reunion.jreunion.server.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.Parser;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class SaveInventory extends DatabaseAction {

	private PreparedStatement deleteStatement;
	private PreparedStatement insertStatement;
	
	public PreparedStatement getDeleteStatement() {
		return deleteStatement;
	}

	public PreparedStatement getInsertStatement() {
		return insertStatement;
	}

	public SaveInventory(Connection connection) {
		super(connection);
		
		try{
			deleteStatement =  new PreparedStatement((MySQLConnection) connection,"DELETE FROM inventory WHERE charid=?");	
			insertStatement =  new PreparedStatement((MySQLConnection) connection,"INSERT INTO inventory (charid, itemid, tab, x, y) VALUES(?,?,?,?,?)");
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
	}

	public void perform() throws SQLException {
		Logger.getLogger(SaveInventory.class).info("perform");
		deleteStatement.execute();
		insertStatement.executeBatch();
	}

}
