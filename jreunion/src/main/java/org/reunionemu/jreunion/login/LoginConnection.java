package org.reunionemu.jreunion.login;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

import org.reunionemu.jreunion.network.PacketConnection;

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
