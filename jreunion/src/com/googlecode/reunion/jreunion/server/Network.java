package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.events.server.ServerEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */

public class Network extends Service implements Runnable, EventListener{
	
	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	private Selector selector;
	
	private Thread thread;
	
	public Network(Server server) {
		super();
		try {
			server.addEventListener(ServerEvent.class, this);		
			selector = Selector.open();
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.setName("network");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}

	@Override
	public void run() {
			
			while (true) {
				try {
					// See if we've had any activity -- either an incoming connection,
					// or incoming data on an existing connection
					int num = selector.select();
					if(num == 0){
						// we need synchronize here otherwise we might block again before we were able to change the selector
						synchronized(this){
							continue;
						}
					}
				
				// If we don't have any activity, loop around and wait again
				// Get the keys corresponding to the activity
				// that has been detected, and process them one by one
				
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while (it.hasNext()) {
					// Get a key representing one of bits of I/O activity
					SelectionKey key = it.next();
					if(!key.isValid())
						continue;
					
					SelectableChannel selectableChannel = key.channel();
					// What kind of activity is it?
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						
						// It's an incoming connection.
						// Register this socket with the Selector
						// so we can listen for input on it						
						
						SocketChannel clientSocketChannel = ((ServerSocketChannel)selectableChannel).accept();
						
						processAccept(clientSocketChannel);
					
					} else {
						SocketChannel socketChannel = (SocketChannel)selectableChannel;
						
						if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						
							
							// It's incoming data on a connection, so process it
							boolean ok = processInput(socketChannel);
		
							// If the connection is dead, then remove it
							// from the selector and close it
							if (!ok) {
								Logger.getLogger(Network.class).info("Client Connection Lost");
								key.cancel();
								disconnect(socketChannel);
							}

						} 
						else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
							
							boolean ok = processOutput(socketChannel);
							if(ok){
								socketChannel.register(selector, SelectionKey.OP_READ);
							}
						}
					}
				}
				// We remove the selected keys, because we've dealt with them.
				keys.clear();
				
			} catch (Exception e) {
				if(e instanceof ClosedSelectorException||e instanceof InterruptedException)
					return;
				Logger.getLogger(Network.class).error("Error in network",e);
			}
		}
	
	}
	
	private void processAccept(SocketChannel socketChannel) throws IOException {
		
		socketChannel.configureBlocking(false);
		
		fireEvent(NetworkAcceptEvent.class, socketChannel);
		
		// Register it with the selector, for reading
		selector.wakeup();
		socketChannel.register(selector, SelectionKey.OP_READ);
		

	}

	private boolean processInput(SocketChannel socketChannel) {

		int result = -1;
		Client client = null;
		buffer.clear();
		try{
			result = socketChannel.read(buffer);
			buffer.flip();
			client = Server.getInstance().getWorld().getClients().get(socketChannel);
		}
		catch(IOException e) {
			Logger.getLogger(this.getClass()).error("Exception",e);
		}
		
		// If no data or client, close the connection
		if (result<=0||client == null) {
			return false;
		}
		
		byte[] data = new byte[result];
		buffer.get(data, 0, result);		
		
		fireEvent(NetworkDataEvent.class, socketChannel, data);
				
		return true;
	}
	
	private boolean processOutput(SocketChannel socketChannel) {

		Client client = Server.getInstance().getWorld().getClients().get(socketChannel);
		
		if(client == null)
			return false;
		
		if(!socketChannel.isOpen()||!socketChannel.isConnected()){
			disconnect(socketChannel);
			return false;
		}
			buffer.clear();			
			byte [] packetBytes = client.flush(); 
			if(packetBytes==null)
				return true;
			buffer.put(packetBytes);
			buffer.flip();			
		
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error("Exception", e);
			disconnect(socketChannel);
			return false;
		}
		return true;
	}
	public void disconnect(SocketChannel socketChannel) {
		
		if(socketChannel.isConnected() && socketChannel.isOpen()) {
			processOutput(socketChannel);
		}
		Logger.getLogger(Network.class).info("Disconnecting " + socketChannel);
		fireEvent(NetworkDisconnectEvent.class, socketChannel);
		
		try {
			socketChannel.close();
		} catch (IOException e) {
			 Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	}
	
	public void notifySend(SocketChannel socketChannel) {
		try {
			 // we synchronize this to make sure we register the key before the selector gets back to sleep again.
			if(socketChannel.isOpen()&&selector.isOpen()){
				selector.wakeup();
				socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				
			}
		
		} catch (ClosedChannelException e) {
			//Disconnect detected
		}catch(CancelledKeyException e){
			
		
		}catch(Exception e){
			
			Logger.getLogger(this.getClass()).error("Exception",e);
			
		}
	}

	public void start() {
		
		thread.start();
		
	}

	public boolean register(InetSocketAddress address) {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannel.socket();
			serverSocket.bind(address);
			serverChannel.configureBlocking(false);
			synchronized(this){
				selector.wakeup();
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			}
			
		} catch (Exception e) {
			if (e instanceof BindException) {
				Logger.getLogger(Network.class).error("Port " + address.getPort()
						+ " not available. Is the server already running?",e);
				return false;
			}
		}
		return true;
	}

	public void stop() {
		try {
			Logger.getLogger(Network.class).info("net stop");
			selector.close();
			thread.interrupt();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	}

	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);
		if(event instanceof NetworkSendEvent){
			NetworkSendEvent networkSendEvent = (NetworkSendEvent) event;
			this.notifySend(networkSendEvent.getSocketChannel());
		}else if(event instanceof ServerStartEvent){
			start();
		}else if(event instanceof ServerStopEvent){
			stop();
		}				
	}
}
