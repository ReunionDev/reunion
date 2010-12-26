package com.googlecode.reunion.jreunion.server;

import java.util.HashMap;
import java.util.Random;
import org.apache.log4j.*;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;

import com.googlecode.reunion.jreunion.events.EventDispatcher;
import com.googlecode.reunion.jreunion.events.Test;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;
import com.googlecode.reunion.jreunion.protocol.Protocol;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Server extends EventDispatcher {

	private static Server _instance = null;
	
	
	
	private static Random rand = new Random(System.currentTimeMillis());
	
	public static Random getRand() {
		return rand;
	}
	
	private State state = State.LOADING;

	public State getState() {
		return state;
	}

	private void setState(State state) {
		this.state = state;
	}

	private HashMap<String,Service> services = new HashMap<String,Service>();

	public HashMap<String, Service> getServices() {
		return services;
	}

	public synchronized static Server getInstance() {
		if (_instance == null) {
			try {
				_instance = new Server();
			} catch (Exception e) {
				Logger.getLogger(Server.class).warn("Exception",e);
				return null;
			}
		}
		return _instance;
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Exception {
		Logger logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new PatternLayout("%-5p [%t]: %m\r\n"){
			
			@Override
			public String format(LoggingEvent event) {

				String result = super.format(event);
				if(result.endsWith("\n\r\n")){
					
					result = result.substring(0, result.length()-2);
				}
				return result;
			}
			
		},ConsoleAppender.SYSTEM_OUT));

		
		Thread.currentThread().setName("main");
		
		PrintStream.useFileLogging();
		Reference.getInstance().Load();
		
		
		Server server = Server.getInstance();
		

		try {

			
			server.database.start();

			Logger.getLogger(Server.class).info("Server start");
			server.fireEvent(server.createEvent(ServerStartEvent.class, server));			
			
								// modules
								// Load a module by extending it from
								// ClassModule
								// And put the put the parent in the constructor
			
			

			System.gc();
			server.setState(State.RUNNING);
			synchronized(server){
				server.wait();
			}
			
			
		} catch (Exception e) {
			
			Logger.getLogger(Server.class).warn("Exception",e);
			
		}
		finally {
			server.setState(State.CLOSING);
			server.fireEvent(server.createEvent(ServerStopEvent.class, server));
			
			Logger.getLogger(Server.class).info("Server stop");
			
			EventDispatcher.shutdown();
			server.database.stop();
			System.exit(-1);
		}
	}

	private Network network;

	private PacketParser packetParser;

	private World world;

	private Database database;

	private Server() throws Exception {

		super();
		
		new Debug();
		RemoteAdmin.enableRemoteAdmin();
		
		Protocol.load();
		
		database = new Database(this);
		network = new Network(this);
		world = new World(this);
		packetParser = new PacketParser();
		
		//network.addEventListener(NetworkDataEvent.class, packetParser);
	}
	
	

	/**
	 * @return Returns the databaseModule.
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * @return Returns the networkModule.
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * @return Returns the packetParser.
	 */
	public PacketParser getPacketParser() {
		return packetParser;
	}

	/**
	 * @return Returns the worldModule.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @param databaseModule
	 *            The databaseModule to set.
	 */
	public void setDatabaseModule(Database databaseModule) {
		this.database = databaseModule;
	}

	
	public static enum State{
		
		LOADING,
		RUNNING,
		CLOSING
		
	}

}
