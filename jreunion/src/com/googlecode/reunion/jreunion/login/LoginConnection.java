package com.googlecode.reunion.jreunion.login;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import com.googlecode.reunion.jreunion.network.Connection;
import com.googlecode.reunion.jreunion.server.packets.Packet;

public class LoginConnection extends Connection<LoginConnection> {

	public LoginConnection(LoginServer loginServer,
			SocketChannel socketChannel) throws IOException {
		super(loginServer, socketChannel);
		
	}

	@Override
	public void onData(ByteBuffer inputBuffer) {
		try {
			byte [] data = getData();
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Packet packet = (Packet) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
