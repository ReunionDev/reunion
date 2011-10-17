package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.googlecode.reunion.jreunion.network.Connection;
import com.googlecode.reunion.jreunion.network.NetworkThread;
import com.googlecode.reunion.jreunion.proxy.ProxyExternalConnection.ConnectionState;
import com.googlecode.reunion.jreunion.server.packets.FailPacket;
import com.googlecode.reunion.jreunion.server.packets.Packet;

public class ProxyExternalNetwork extends NetworkThread<ProxyExternalConnection> {
	
	InetSocketAddress internalAddress;
	InetSocketAddress externalAddress;
	
	private int sessionIter = 0;
	
	private Map<Integer, ProxyExternalConnection> sessions = new HashMap<Integer, ProxyExternalConnection>();
	
	public ProxyExternalNetwork(InetSocketAddress internal, InetSocketAddress external) throws IOException {
		this.internalAddress = internal;
		this.externalAddress = external;
		bind(internal);
	}
		
	@Override
	public void onConnect(ProxyExternalConnection connection) {
		boolean success = connection.getSocketChannel().isConnected();
		
		System.out.println(connection.getSocketChannel()+" "+(success?"connected":"connection failed"));
		if(success){
			connection.close();
		}
	}
	
	public void onPacketReceived(ProxyExternalConnection connection, Packet packet){
		if(connection.getState()==ConnectionState.GAME_SERVER){
			
		} else {
			connection.write(new FailPacket("Login Server offline."));
		}
	}
	
	@Override
	public ProxyExternalConnection createConnection(SocketChannel socketChannel) {
		try {
			return new ProxyExternalConnection(this, socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onAccept(ProxyExternalConnection connection) {
		System.out.println(connection.getSocketChannel().socket()+" accepted");
		synchronized(sessions){
			while(sessions.keySet().contains(sessionIter)){
				if(sessionIter < Integer.MAX_VALUE){
					sessionIter++;
				}else{
					sessionIter = 0;
				}
			}
			sessions.put(sessionIter, connection);
			connection.setSessionId(sessionIter);
		}
	}

	@Override
	public void onDisconnect(ProxyExternalConnection connection) {
		System.out.println(connection.getSocketChannel().socket()+" closed");
		synchronized(sessions){
			Iterator<Entry<Integer, ProxyExternalConnection>> iterator = sessions.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer, ProxyExternalConnection> entry = iterator.next();
				if(entry.getValue()==connection){
					iterator.remove();
					// Handle disconnect
				}
			}
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
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
		InetSocketAddress internal = new InetSocketAddress(4005);
		InetSocketAddress external = new InetSocketAddress(InetAddress.getByName("192.168.1.199"), 4005);
		ProxyExternalNetwork proxy = new ProxyExternalNetwork(internal, external);
		proxy.start();
		Connection<ProxyExternalConnection> connection = proxy.connect(new InetSocketAddress(4005));
		
		System.out.println(connection.getSocketChannel());
			
	}
}
