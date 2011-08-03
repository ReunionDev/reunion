package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import org.omg.IOP.Encoding;

import com.googlecode.reunion.jreunion.network.Connection;
import com.googlecode.reunion.jreunion.network.NetworkThread;

public class Proxy extends NetworkThread {
	
	InetSocketAddress internal;
	InetSocketAddress external;
	
	
	public Proxy(InetSocketAddress internal, InetSocketAddress external) throws IOException {
		super(internal);
		this.internal = internal;
		this.external = external;
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		InetSocketAddress internal = new InetSocketAddress(4005);
		InetSocketAddress external = new InetSocketAddress(InetAddress.getByName("192.168.1.199"), 4005);
		Proxy proxy = new Proxy(internal, external);
		proxy.start();
		
		
		Thread.sleep(1000);
		Socket s = new Socket(InetAddress.getLocalHost(), 4005);
		s.getOutputStream().write("test".getBytes());
		Thread.sleep(3000);
		s.getOutputStream().write("test".getBytes());
		s.close();		
	}
	
	@Override
	public Connection createConnection(SocketChannel socketChannel) {
		try {
			return new ProxyConnection(this, socketChannel);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void onAccept(Connection connection) {
		System.out.println(connection.getSocketChannel().socket()+" accepted");
		connection.write("hello".getBytes());
		
	}

	@Override
	public void onDisconnect(Connection connection) {
		System.out.println(connection.getSocketChannel().socket()+" disconnected");
		
	}

}
