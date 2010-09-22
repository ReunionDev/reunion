package com.googlecode.reunion.jreunion.server;

import java.util.HashMap;

import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Server extends ClassModule {

	private static Server _instance = null;
	
	private HashMap<String,Service> services = new HashMap<String,Service>();

	public HashMap<String, Service> getServices() {
		return services;
	}

	public synchronized static Server getInstance() {
		if (_instance == null) {
			try {
				_instance = new Server();
			} catch (Exception e) {

				e.printStackTrace();
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

		Server server = Server.getInstance();

		try {

			server.fireEvent(server.createEvent(ServerStartEvent.class, server));
			server.doStart(); // Call the start functions in all the loaded
								// modules
								// Load a module by extending it from
								// ClassModule
								// And put the put the parent in the constructor
			
			
			
			while (true) {
				server.doWork();
				Thread.sleep(1); // Sleep to make sure it doesnt use 100%
									// cpu resources
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		finally {
			PerformanceStats.getInstance().dumpPerformance();
			server.fireEvent(server.createEvent(ServerStopEvent.class, server));
			server.doStop();
			System.exit(-1);
		}
	}

	private Network networkModule;

	private PacketParser packetParser;

	private World worldModule;

	private Database databaseModule;

	private Server() throws Exception {

		super();
		RemoteAdmin.enableRemoteAdmin();
		PrintStream.useFileLogging();
		PerformanceStats.createPerformanceStats(this);
		Reference.getInstance().Load();

		databaseModule = new Database(this);
		networkModule = new Network(this);
		worldModule = new World(this);
		packetParser = new PacketParser(this);
	}
	
	

	/**
	 * @return Returns the databaseModule.
	 */
	public Database getDatabaseModule() {
		return databaseModule;
	}

	/**
	 * @return Returns the networkModule.
	 */
	public Network getNetworkModule() {
		return networkModule;
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
	public World getWorldModule() {
		return worldModule;
	}

	/**
	 * @param databaseModule
	 *            The databaseModule to set.
	 */
	public void setDatabaseModule(Database databaseModule) {
		this.databaseModule = databaseModule;
	}


	@Override
	public void start() {
		System.out.println("Server start");
		
		
	}

	@Override
	public void stop() {
		System.out.println("Server stop");
	}

	@Override
	public void Work() {
		// System.out.println("server work");
	}

}
