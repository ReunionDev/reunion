package com.googlecode.reunion.jreunion.login;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.network.PacketConnection;

public class LoginConnection extends PacketConnection<LoginConnection> {

	public LoginConnection(LoginServer loginServer,
			SocketChannel socketChannel) throws IOException {
		super(loginServer, socketChannel);
		
	}

	@Override
	public void onPacket(Serializable obj) {
		LoginServer server = (LoginServer) getNetworkThread();
	}
}
