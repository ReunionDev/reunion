package com.googlecode.reunion.jreunion.login;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.googlecode.reunion.jreunion.login.handlers.LoginHandler;
import com.googlecode.reunion.jreunion.network.NetworkThread;
import com.googlecode.reunion.jreunion.server.packets.Packet;

public class LoginServer extends NetworkThread<LoginConnection> {
	
	List<LoginConnection> connections = new LinkedList<LoginConnection>();
	
	private Map<Class<?>,List<LoginHandler>> handlers = new HashMap<Class<?>,List<LoginHandler>>();

	public LoginServer(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}
	
	@Override
	public void onAccept(LoginConnection connection) {
		connections.add(connection);
		
	}

	@Override
	public void onDisconnect(LoginConnection connection) {
		connections.remove(connection);
	}
	
	private void loadhandlers(){
		handlers.clear();
		for(Class<? extends LoginHandler> handler: LoginHandler.findAllHandlers()){
			
			
		}
	}
	
	public void handlePacket(LoginConnection connection, Packet packet){
		if(handlers==null){
			loadhandlers();
		}
		
	}

	@Override
	public LoginConnection createConnection(SocketChannel socketChannel) {
		try {
			return new LoginConnection(this, socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
