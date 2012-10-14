package org.reunionemu.jreunion.server;

import java.sql.Connection;

import javax.sql.DataSource;

import org.reunionemu.jreunion.server.beans.SpringApplicationContext;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Database {

	public Connection dinamicConn = null; //reunion database
	
	public Connection staticConn = null; //reunionStatic database
	
	public Database(Server server) {
		
	}

	public void connectDinamic() throws Exception {
		
		DataSource datasource = SpringApplicationContext.getApplicationContext().getBean(DataSource.class);
		
		DatabaseUtils.getDinamicInstance().setDinamicDatabase(this); 
		dinamicConn = datasource.getConnection();
		LoggerFactory.getLogger(Database.class).info("Dinamic "+getClass().getSimpleName() + " connection established");

	}

	public void start(){

		try {
			connectDinamic();
		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}

	public void stop() {

		if (dinamicConn != null) {
			try {
				dinamicConn.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
		if (staticConn != null) {
			try {
				staticConn.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

}
