package com.googlecode.reunion.jreunion.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class PacketConnection<T extends Connection<T>> extends Connection<T> {

	public PacketConnection(NetworkThread<T> networkThread,
			SocketChannel socketChannel) {
		super(networkThread, socketChannel);
	}
	
	@Override
	public void onData(ByteBuffer inputBuffer) {
		System.out.println("onData");
		byte [] data = getData();
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream in = new ObjectInputStream(bais);
			while(bais.available()>0){
				Object obj = in.readObject();
				if(obj!=null){
					onPacket((Serializable)obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}	
	
	public void writePacket(Serializable obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		write(baos.toByteArray());
	}
	
	public abstract void onPacket(Serializable obj);

}
