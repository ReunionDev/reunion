package org.reunionemu.jreunion.server;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */

@Service
public class Database {

	public Connection dinamicConn = null; //reunion database
	
	@Autowired
	DataSource datasource;
	
	public Database() {
		
	}

	public void connectDinamic() throws Exception {
		
		//DataSource datasource = SpringApplicationContext.getApplicationContext().getBean(DataSource.class);
		
		DatabaseUtils.getDinamicInstance().setDinamicDatabase(this); 
		dinamicConn = datasource.getConnection();
		LoggerFactory.getLogger(Database.class).info("Dinamic "+getClass().getSimpleName() + " connection established");

	}

	@PostConstruct
	public void start(){

		try {
			connectDinamic();
		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}

	@PreDestroy
	public void stop() {

		if (dinamicConn != null) {
			try {
				dinamicConn.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
				
			}
		}
	}

}
