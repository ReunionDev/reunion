package org.reunionemu.jreunion.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import org.reunionemu.jreunion.network.Connection;
import org.reunionemu.jreunion.network.NetworkThread;
import org.reunionemu.jreunion.proxy.ProxyConnection.ConnectionState;
import org.reunionemu.jreunion.server.packets.FailPacket;
import org.reunionemu.jreunion.server.packets.Packet;

public class ProxyServer extends NetworkThread<ProxyConnection> {
	
	InetSocketAddress internal;
	InetSocketAddress external;
	
	private int sessionIter = 0;
	
	private Map<Integer, ProxyConnection> sessions = new HashMap<Integer, ProxyConnection>();
	
	public ProxyServer(InetSocketAddress internal, InetSocketAddress external) throws IOException {
		this.internal = internal;
		this.external = external;
		bind(internal);
	}
		
	@Override
	public void onConnect(ProxyConnection connection) {
		boolean success = connection.getSocketChannel().isConnected();
		
		System.out.println(connection.getSocketChannel()+" "+(success?"connected":"connection failed"));
		if(success){
			connection.close();
		}
	}
	
	public void onPacketReceived(ProxyConnection connection, Packet packet){
		if(connection.getState()==ConnectionState.GAME_SERVER){
			
		} else {
			connection.write(new FailPacket("Login Server offline."));
		}
	}
	
	@Override
	public ProxyConnection createConnection(SocketChannel socketChannel) {
		try {
			return new ProxyConnection(this, socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onAccept(ProxyConnection connection) {
		System.out.println(connection.getSocketChannel().socket()+" accepted");
		synchronized(sessions){
			while(sessions.keySet().contains(sessionIter)){
				if(sessionIter<Integer.MAX_VALUE){
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
	public void onDisconnect(ProxyConnection connection) {
		System.out.println(connection.getSocketChannel().socket()+" closed");
		synchronized(sessions){
			Iterator<Entry<Integer, ProxyConnection>> iterator = sessions.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer, ProxyConnection> entry = iterator.next();
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
		
		BasicConfigurator.configure();

		InetSocketAddress internal = new InetSocketAddress(4005);
		InetSocketAddress external = new InetSocketAddress(InetAddress.getByName("192.168.1.199"), 4005);
		ProxyServer proxy = new ProxyServer(internal, external);
		proxy.start();
		Connection connection = proxy.connect(new InetSocketAddress(4005));
		
		System.out.println(connection.getSocketChannel());
			
	}
}
