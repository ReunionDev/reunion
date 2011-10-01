package com.googlecode.reunion.jreunion.login;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.network.NetworkThread;

public class LoginServer extends NetworkThread<LoginConnection> {
	
	

	public LoginServer(int port) throws IOException {
		bind(new InetSocketAddress(port));
	}
	
	@Override
	public void onAccept(LoginConnection connection) {
		
		
	}

	@Override
	public void onDisconnect(LoginConnection connection) {
		
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
