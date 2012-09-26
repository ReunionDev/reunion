package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.Random;

import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.server.ServerStartEvent;
import org.reunionemu.jreunion.events.server.ServerStopEvent;
import org.reunionemu.jreunion.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@Service
public class Server extends EventDispatcher {

	private static Server _instance = null;	
	
	private static AbstractApplicationContext context;
	
	private static Random rand = new Random(System.currentTimeMillis());
	
	
	public static Logger logger = LoggerFactory.getLogger(Server.class);
	
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
				_instance = context.getBean(Server.class);
			} catch (Exception e) {
				logger.error("Exception",e);
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
		
		context = new ClassPathXmlApplicationContext("classpath*:/META-INF/spring/**/*-context.xml");

		context.registerShutdownHook();
		
		
		
		Thread.currentThread().setName("main");
		
		//Reference.getInstance().Load();
		
		
		
		Server server = Server.getInstance();

		try {

			//server.database.start();
			

			logger.info("Server start");
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
			
			logger.error("Exception",e);
			
		}
		finally {
			
			server.setState(State.CLOSING);
			server.fireEvent(server.createEvent(ServerStopEvent.class, server));
			
			logger.info("Server stop");
			
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
		
		Protocol.load();
		
		database = new Database(this);
		database.start();
		
		logger.info("Loading server objects...");
		Reference.getInstance().Load();
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