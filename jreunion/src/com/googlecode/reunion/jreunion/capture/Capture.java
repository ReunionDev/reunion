package com.googlecode.reunion.jreunion.capture;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.Marshaller.Listener;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Network;
import com.googlecode.reunion.jreunion.server.Server;

public class Capture extends Thread{

	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	public Capture() throws UnknownHostException {
		address = InetAddress.getByName("127.0.0.1");
	}
	@Override
	public void run() {
		try {

			Selector selector = Selector.open();
			
			
			
			System.out.println("Proxy started for "+address+":"+port);
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("started listening");
			selector.select();			
			SocketChannel local = serverSocketChannel.accept();
			
			
			
			System.out.println("connection accepted");
			serverSocketChannel.close();
			SocketChannel remote = SocketChannel.open(new InetSocketAddress(address, port));
			
			remote.configureBlocking(false);
			local.configureBlocking(false);
			
			local.register(selector, SelectionKey.OP_READ);
			remote.register(selector, SelectionKey.OP_READ);
			
			while(true) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					if(!key.isValid())
						continue;
					SocketChannel socketChannel = (SocketChannel)key.channel();
					if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						
						buffer.clear();
						socketChannel.read(buffer);
						buffer.flip();
					
						int size = buffer.limit();
						if (size == 0) {
							continue;
						}
						byte[] data = new byte[size];
						buffer.get(data, 0, size);		
						
						
						System.out.println("Received "+size +" bytes from "+socketChannel);
						SocketChannel target = socketChannel.equals(local)?remote:local;						
						target.register(selector, SelectionKey.OP_WRITE, data);
						
					}else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
						buffer.clear();			
						buffer.put((byte[])key.attachment());
						buffer.flip();
						System.out.println("Sending "+buffer.limit() +" bytes to "+socketChannel);
						socketChannel.write(buffer);
						socketChannel.register(selector, SelectionKey.OP_READ);
						
					}
				}
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InetAddress address = null;
	public int port = 4005;
	public int version = 2000;
	public int mapId = 4;
	/**
	 * @param args
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		
		
		Capture capture = new Capture();
		if(args.length>0){
			capture.address = InetAddress.getByName(args[0]);
		}
		if(args.length>1){
			capture.port = Short.parseShort(args[1]);
		}
		if(args.length>2){
			capture.version = Short.parseShort(args[2]);
		}
		if(args.length>3){
			capture.mapId = Short.parseShort(args[3]);
		}
		capture.start();
		capture.join();
	}

}
