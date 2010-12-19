package com.googlecode.reunion.jreunion.capture;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.googlecode.reunion.jreunion.protocol.OtherProtocol;

public class Capture extends Thread{

	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	
	private HashMap<Integer,Integer> maps = new HashMap<Integer,Integer>();
	private HashMap<Integer,SocketChannel> localRoutes = new HashMap<Integer,SocketChannel>();
	private HashMap<Integer,SocketChannel> remoteRoutes = new HashMap<Integer,SocketChannel>();
	private HashMap<Integer,OtherProtocol> protocols = new HashMap<Integer,OtherProtocol>();
	private HashMap<Integer,OtherProtocol> remoteProtocols = new HashMap<Integer,OtherProtocol>();
	
	BufferedOutputStream bos;
	
	public Capture() throws Exception {
		
		version = 2000;

		bos = new BufferedOutputStream(new FileOutputStream("output-"+(new Date().getTime()/1000)+".txt", true));
		
		maps.put(4005, 4);
		maps.put(4001, 0);
		
		address = InetAddress.getByName("93.90.190.134");
		
	}	
	public void run() {
		try {

			Selector selector = Selector.open();
			
			for(int port: maps.keySet()){
				int mapId = maps.get(port);
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port));
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

				System.out.println("Proxy started for "+address+":"+port);
				
			}
						
			System.out.println("started listening");
			
			while(true) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					if(!key.isValid())
						continue;
					
					SelectableChannel selectableChannel = key.channel();
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectableChannel;
						SocketChannel socketChannel = serverSocketChannel.accept();
						if(socketChannel==null)
							continue;
						int port = socketChannel.socket().getLocalPort();
						int mapId = maps.get(port);
						{
							OtherProtocol protocol = new OtherProtocol(null);
							protocol.setAddress(address);
							protocol.setVersion(version);
							protocol.setMapId(mapId);
							protocol.setPort(port);
							protocols.put(port, protocol);
						}
						/*
						{
							OtherProtocol protocol = new OtherProtocol(null);
							protocol.setAddress(address);
							protocol.setVersion(version);
							protocol.setMapId(mapId);
							protocol.setPort(port);
							remoteProtocols.put(port, protocol);
						
						}
						*/
						
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
						
						localRoutes.put(port, socketChannel);
						
						socketChannel = SocketChannel.open(new InetSocketAddress(address,port));
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
						
						remoteRoutes.put(port, socketChannel);
						
						System.out.println("rerouted "+ port);
						
					}else{
						
						SocketChannel socketChannel = (SocketChannel)selectableChannel;
						if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
							
							buffer.clear();
							int size =socketChannel.read(buffer);
							buffer.flip();
							if (size <= 0) {
								continue;
							}
							byte[] data = new byte[size];
							buffer.get(data, 0, size);
							System.out.println("Received "+size +" bytes from "+socketChannel);
							boolean local = localRoutes.containsValue(socketChannel);
							int port = local?socketChannel.socket().getLocalPort():socketChannel.socket().getPort();
							SocketChannel target = local?remoteRoutes.get(port):localRoutes.get(port);
							
							target.register(selector, SelectionKey.OP_WRITE, data);
							OtherProtocol protocol = protocols.get(port);
							//OtherProtocol protocol = local?remoteProtocols.get(port):localProtocols.get(port);
							String output = null;
							if(local){								
								
								output = "From client:\n"+protocol.decryptServer(data.clone());								
							} else {
								output = "From server:\n"+protocol.decryptClient(data.clone());
								
							}
							
							System.out.println(output);
							bos.write(output.getBytes());
							bos.flush();
														
							
						}else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
							
							buffer.clear();		
							byte [] data = (byte[])key.attachment();
							if(data!=null){
								buffer.put((byte[])key.attachment());
								buffer.flip();
								System.out.println("Sending "+buffer.limit() +" bytes to "+socketChannel);
								socketChannel.write(buffer);
							}
							socketChannel.register(selector, SelectionKey.OP_READ);
						}
					}
				}
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress address;
	public int version;
	public int mapId;
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
			capture.version = Short.parseShort(args[2]);
		}
		if(args.length>2){
			capture.mapId = Short.parseShort(args[3]);
		}
		capture.start();
		capture.join();
	}

}
