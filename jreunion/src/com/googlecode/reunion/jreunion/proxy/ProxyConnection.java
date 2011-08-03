package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.network.Connection;
import com.googlecode.reunion.jreunion.network.NetworkThread;
import com.sun.xml.internal.fastinfoset.Encoder;

public class ProxyConnection extends Connection {

	public ProxyConnection(NetworkThread selectorThread,
			SocketChannel socketChannel) throws IOException {
		super(selectorThread, socketChannel);
		
	}
	public void onData(ByteBuffer inputBuffer) 
	{
		byte [] data = getData();
		
		System.out.println(new String(data));
		
	}

}
