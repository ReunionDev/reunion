package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientSendEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.events.server.ServerEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;
import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Network extends Service implements Runnable, EventListener{
	
	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	private Selector selector;
	
	private Thread thread;

	List<Socket> sockets = new Vector<Socket>();
	
	public Network(Server server) {
		super();
		try {
			server.addEventListener(ServerEvent.class, this);		
			selector = Selector.open();
			thread = new Thread(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
	}

	@Override
	public void run() {
		try {
			
			while (true) {
				// See if we've had any activity -- either an incoming connection,
				// or incoming data on an existing connection
					int num = selector.select();
					if(num==0){
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
					SelectableChannel socketChannel = key.channel();
					
					// What kind of activity is it?
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						
						// It's an incoming connection.
						// Register this socket with the Selector
						// so we can listen for input on it						
						
						SocketChannel clientSocketChannel = ((ServerSocketChannel)socketChannel).accept();
						clientSocketChannel.configureBlocking(false);
						Socket socket = clientSocketChannel.socket();		
										
						System.out.print("Got connection from " + socket+"\n");
						
						fireEvent(NetworkAcceptEvent.class,socket);
						
						// Register it with the selector, for reading
						clientSocketChannel.register(selector, SelectionKey.OP_READ);
					} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						
						try {
							// It's incoming data on a connection, so process it
							boolean ok = processInput((SocketChannel) socketChannel);

							// If the connection is dead, then remove it
							// from the selector and close it
							if (!ok) {
								System.out
								.println("Client Connection Lost");
								throw new IOException("Connection Lost");

							}

						} catch (IOException ie) {

							// On exception, remove this channel from the selector
							key.cancel();
							Socket socket = ((SocketChannel) socketChannel).socket();
							
							disconnect(socket);
							
						}
					} else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
						
						boolean ok = processOutput((SocketChannel) socketChannel);
						if(ok){
							socketChannel.register(selector, SelectionKey.OP_READ);
						}
					}
				}
				// We remove the selected keys, because we've dealt with them.
				keys.clear();
			}
		} catch (Exception ie) {
			if(ie instanceof ClosedSelectorException)
				return;
			System.err.println(ie);
		}
	}
	
	private boolean processInput(SocketChannel socketChannel) throws IOException {

		buffer.clear();
		socketChannel.read(buffer);
		buffer.flip();
		Socket socket = socketChannel.socket();
		Client client = Server.getInstance().getWorld().getClients().get(socket);
		
		if (client == null) {
			return false;
		}
		// If no data, close the connection
		if (buffer.limit() == 0) {
			return false;
		}
		
		int size = buffer.limit();
		PerformanceStats.getInstance().receivedPacket(buffer.limit());
		byte[] output = new byte[size];
		buffer.get(output, 0, size);

		System.out.print(client + " sends: \n");

		char[] decOutput = Crypt.getInstance().C2Sdecrypt(output);

		String data = new String(decOutput);
		
		System.out.print(data);
		
		fireEvent(NetworkDataEvent.class, socket, data);
		
		//S_Server.getInstance().getPacketParser().Parse(client,data);
		return true;
	}
	
	private boolean processOutput(SocketChannel socketChannel) throws IOException {
		
		
		Socket socket = socketChannel.socket();
		Client client = Server.getInstance().getWorld().getClients().get(socket);
		if (client == null) {
			return false;
		}
		synchronized(client){
			
			StringBuffer outputBuffer = client.getOutputBuffer();
			PerformanceStats.getInstance().sentPacket(outputBuffer.length());
			
			buffer.clear();
			String packetData = outputBuffer.toString();
			byte [] packetBytes = Crypt.getInstance().S2Cencrypt(
					packetData.toCharArray());
			buffer.put(packetBytes);
			buffer.flip();
			outputBuffer.setLength(0);
			System.out.print("Sending to "+client+":\n" + packetData);
		}
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			disconnect(socket);				
		}
		return true;
	}
	public void disconnect(Socket socket) {
		
		System.out.println("Disconnecting " + socket);
		fireEvent(NetworkDisconnectEvent.class, socket);
		try {
			socket.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
	
	public void notifySend(Socket socket) {
		try {
			synchronized(this){
				if(socket.getChannel().isOpen()){
					selector.wakeup();					
					socket.getChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			}
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}		
	}

	public void start() {
		
		thread.start();
		
	}

	public void register(InetSocketAddress address) {
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
				System.out.println("Port " + address.getPort()
						+ " not available. Is the server already running?");
				throw new RuntimeException(e);
			}
		}
	}

	public void stop() {
		try {
			System.out.println("net stop");
		
			for(Socket socket : sockets){				
				disconnect(socket);
			}
			sockets.clear();
			selector.close();
			thread.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);
		if(event instanceof NetworkSendEvent){
			NetworkSendEvent networkSendEvent = (NetworkSendEvent) event;
			this.notifySend(networkSendEvent.getSocket());
		}else if(event instanceof ServerStartEvent){
			start();
		}else if(event instanceof ServerStopEvent){
			stop();
		}				
	}
}
