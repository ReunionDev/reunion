package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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

import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.NetworkDataEvent;
import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Network extends EventBroadcaster implements Runnable{
	
	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	Map<Socket,S_Client> clients = new Hashtable<Socket, S_Client>();

	private Selector selector;
	
	private Thread thread;

	public S_Network() {
		
		try {
			selector = Selector.open();
			thread = new Thread(this);
		} catch (IOException e) {
			e.printStackTrace();
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
						S_Client client = new S_Client();
						
						SocketChannel clientSocketChannel = ((ServerSocketChannel)socketChannel).accept();
						Socket socket = clientSocketChannel.socket();						
						
						client.setSocket(socket);
						System.out.print("Got connection from " + socket+"\n");
				
						client.setState(S_Client.State.ACCEPTED);
						// Make sure to make it non-blocking, so we can use a selector on it.
						clientSocketChannel.configureBlocking(false);
						clients.put(socket, client);						
						
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
							S_Client client = clients.get(socket);
							disconnect(client);
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
		} catch (IOException ie) {
			System.err.println(ie);
		}
	}
	
	private boolean processInput(SocketChannel socketChannel) throws IOException {

		buffer.clear();
		socketChannel.read(buffer);
		buffer.flip();
		Socket socket = socketChannel.socket();
		S_Client client = clients.get(socket);
		
		if (client == null) {
			return false;
		}
		// If no data, close the connection
		if (buffer.limit() == 0) {
			return false;
		}
		
		int size = buffer.limit();
		S_PerformanceStats.getInstance().receivedPacket(buffer.limit());
		byte[] output = new byte[size];
		buffer.get(output, 0, size);

		System.out.print(client + " sends: \n");

		char[] decOutput = S_Crypt.getInstance().C2Sdecrypt(output);

		String data = new String(decOutput);
		
		System.out.print(data);
		System.out.print(listeners.size());
		fireEvent(new NetworkDataEvent(client, data));
		
		//S_Server.getInstance().getPacketParser().Parse(client,data);
		return true;
	}
	
	private boolean processOutput(SocketChannel socketChannel) throws IOException {
		
		S_Client client = clients.get(socketChannel.socket());		
		if (client == null) {
			return false;
		}
		synchronized(client){
			
			StringBuffer outputBuffer = client.getOutputBuffer();
			S_PerformanceStats.getInstance().sentPacket(outputBuffer.length());
			
			buffer.clear();
			String packetData = outputBuffer.toString();
			byte [] packetBytes = S_Crypt.getInstance().S2Cencrypt(
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
			disconnect(client);				
		}
		return true;
	}
	
	public void disconnect(S_Client client) {
		
		if(client!=null) {
			Socket socket= client.getSocket();
			System.out
			.println("Disconnecting " + client);
			try {
				socket.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			if (client.getPlayer() != null) {
				client.getPlayer().logout();			
			}
			clients.remove(socket);
		}
	}

	public S_Client getClient(G_Player player) {
		Iterator<S_Client> iter = getClientIterator();
		while (iter.hasNext()) {
			S_Client client = iter.next();
			if (client.getPlayer() == player) {
				return client;
			}
		}
		return null;
	}

	public Iterator<S_Client> getClientIterator() {
		return clients.values().iterator();
	}


	
	public void notifySend(S_Client client) {
		try {
			synchronized(this){
				System.out.println("sync selector notify");
				selector.wakeup();
				client.getSocket().getChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
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
		List<S_Client> tmp =  new Vector<S_Client>(clients.values());
		
		for(S_Client client : tmp){				
			disconnect(client);			
		}
			selector.close();
			thread.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
