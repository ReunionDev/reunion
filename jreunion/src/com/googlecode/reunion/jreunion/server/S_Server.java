package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Server extends S_ClassModule {

	private static S_Server _instance = null;
	
	
	List<S_World> worlds = new ArrayList<S_World>();

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
			/*
			 * int x= 120,y=1028;
			 * System.out.println(server.getWorldModule().getMap
			 * ().getPlayerArea(
			 * ).get(x,y)+" "+server.getWorldModule().getMap().getMobArea
			 * ().get(x
			 * ,y)+" "+server.getWorldModule().getMap().getPvpArea().get(x,y));
			 * System
			 * .out.println(server.getWorldModule().getMap().getPvpArea().get
			 * (800,400));
			 * System.out.println(server.getWorldModule().getMap().getPvpArea
			 * ().get(800,250));
			 * System.out.println(server.getWorldModule().getMap
			 * ().getPvpArea().get(800,500));
			 */
			/*
			 * BufferedWriter filebuffer = new BufferedWriter(new
			 * FileWriter("coltest1.txt", false)); for (int y = 0; y<1280;y++)
			 * for (int x = 0; x<1280;x++) { if
			 * (server.getWorldModule().getMapManager
			 * ().getPlayerArea().get(x,y)==true) filebuffer.write("X"); else
			 * filebuffer.write(" ");
			 * 
			 * if( x==1279)filebuffer.write("\n");
			 * 
			 * }
			 * System.out.println(server.getWorldModule().getMapManager().getPvpArea
			 * ().get(702,481)); filebuffer.close();
			 */
			while (true) {

				server.DoWork();
				Thread.sleep(1); // Sleep to make sure it doesnt use 100%
									// cpu resources
				
			}

		} catch (Exception e) {

			server.DoStop();
			S_PerformanceStats.getInstance().dumpPerformance();
			e.printStackTrace();
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
