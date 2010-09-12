package com.googlecode.reunion.jreunion.server;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Server extends S_ClassModule {

	private static S_Server _instance = null;

	public synchronized static S_Server getInstance() {
		if (_instance == null) {
			try {
				_instance = new S_Server();
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

		S_Server server = S_Server.getInstance();

		try {

			server.DoStart(); // Call the start functions in all the loaded
								// modules
								// Load a module by extending it from
								// ClassModule
								// And put the put the parent in the constructor
			while (true) {
				server.DoWork();
				Thread.sleep(1); // Sleep to make sure it doesnt use 100%
									// cpu resources
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		finally {
			S_PerformanceStats.getInstance().dumpPerformance();
			server.DoStop();
			System.exit(-1);
		}
	}

	private S_Network networkModule;

	private S_PacketParser packetParser;

	private S_World worldModule;

	private S_Database databaseModule;

	public S_Server() throws Exception {

		super();
		S_RemoteAdmin.enableRemoteAdmin();
		S_PrintStream.useFileLogging();
		S_PerformanceStats.createPerformanceStats(this);
		S_Reference.getInstance().Load();

		databaseModule = new S_Database(this);
		networkModule = new S_Network(this);
		worldModule = new S_World(this);
		packetParser = new S_PacketParser(this);
	}

	/**
	 * @return Returns the databaseModule.
	 */
	public S_Database getDatabaseModule() {
		return databaseModule;
	}

	/**
	 * @return Returns the networkModule.
	 */
	public S_Network getNetworkModule() {
		return networkModule;
	}

	/**
	 * @return Returns the packetParser.
	 */
	public S_PacketParser getPacketParser() {
		return packetParser;
	}

	/**
	 * @return Returns the worldModule.
	 */
	public S_World getWorldModule() {
		return worldModule;
	}

	/**
	 * @param databaseModule
	 *            The databaseModule to set.
	 */
	public void setDatabaseModule(S_Database databaseModule) {
		this.databaseModule = databaseModule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see server.Module#Start()
	 */
	@Override
	public void Start() {
		System.out.println("S_Server start");
	}

	@Override
	public void Stop() {
		System.out.println("S_Server stop");
	}

	@Override
	public void Work() {
		// System.out.println("server work");
	}

}
