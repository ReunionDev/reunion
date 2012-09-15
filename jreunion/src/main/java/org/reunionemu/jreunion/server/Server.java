package org.reunionemu.jreunion.server;

import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.server.ServerStartEvent;
import org.reunionemu.jreunion.events.server.ServerStopEvent;
import org.reunionemu.jreunion.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Server extends EventDispatcher {

	private static Server _instance = null;	
	
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
				_instance = new Server();
			} catch (Exception e) {
				logger.warn("Exception",e);
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
		
		logger.debug("test");
	     //BasicConfigurator.configure();
		/*
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
		*/
		
		Thread.currentThread().setName("main");
		
		PrintStream.useFileLogging();
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
		
		//new Debug();
		RemoteAdmin.enableRemoteAdmin();
		
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