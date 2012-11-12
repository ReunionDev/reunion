package org.reunionemu.jreunion.server;

import java.util.Random;

import javax.annotation.*;

import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.server.*;
import org.reunionemu.jreunion.protocol.old.Protocol;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.context.support.*;
import org.springframework.stereotype.Service;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */

@DependsOn("database")
@Lazy(false)
@Service
public class Server extends EventDispatcher implements ApplicationContextAware{

	private static Server _instance = null;
		
	private static ApplicationContext context;
	
	@Autowired 
	Reference reference;
	
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
	
	public static ApplicationContext getContext() {
		
		return context;
	}
	
	@PostConstruct
	public void initIt() throws Exception {

		Thread.currentThread().setName("main");

		logger.info("Server start");
		
		Protocol.load();		
		
		logger.info("Loading server objects...");

		packetParser = new PacketParser();
		
		this.fireEvent(this.createEvent(ServerStartEvent.class, this));			
		
		this.setState(State.RUNNING);
	
	}
	
	@PreDestroy
	public void cleanUp() {
		this.setState(State.CLOSING);
		this.fireEvent(this.createEvent(ServerStopEvent.class, this));
		
		logger.info("Server stop");
		
		EventDispatcher.shutdown();
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Exception {
		
		context = new ClassPathXmlApplicationContext("classpath*:/spring/**/*-context.xml");

		((AbstractApplicationContext) context).registerShutdownHook();
		
		
		//Reference.getInstance().Load();
		
		Server server = Server.getInstance();

		try {

			//server.database.start();
		
			//server.fireEvent(server.createEvent(ServerStartEvent.class, server));			
			
								// modules
								// Load a module by extending it from
								// ClassModule
								// And put the put the parent in the constructor
			
			System.gc();
			//server.setState(State.RUNNING);
			synchronized(server){
				server.wait();
			}
			
		} catch (Exception e) {
			
			logger.error("Exception",e);
			
		}
	}

	@Autowired
	private Network network;

	private PacketParser packetParser;

	@Autowired
	private World world;

	private Server() {
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
	
	public static enum State{
		LOADING,
		RUNNING,
		CLOSING
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		Server.context = context;
	}

}