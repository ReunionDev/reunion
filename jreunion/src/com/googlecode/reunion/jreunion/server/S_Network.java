package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.server.S_Enums.S_ClientState;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Network extends S_ClassModule {
	
	Queue<S_PacketQueueItem> queue = new LinkedList<S_PacketQueueItem>();

	private final ByteBuffer buffer = ByteBuffer.allocate(16384);
	
	Map<Socket,S_Client> clients = new Hashtable<Socket,S_Client>();
	
	public List<ServerSocketChannel> channels = new Vector<ServerSocketChannel>();

	private Selector selector;

	public S_Network(S_Module parent) {
		super(parent);
	}

	private void CheckInbound() {
		try {

			while (true) {
				// See if we've had any activity -- either
				// an incoming connection, or incoming data on an
				// existing connection

				int num = selector.selectNow();

				// If we don't have any activity, loop around and wait
				// again
			
				if (num == 0) {
					return;
				}
				
				// Get the keys corresponding to the activity
				// that has been detected, and process them
				// one by one
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while (it.hasNext()) {
					// Get a key representing one of bits of I/O
					// activity
					SelectionKey key = it.next();

					// What kind of activity is it?
					if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						
						// It's an incoming connection.
						// Register this socket with the Selector
						// so we can listen for input on it
						S_Client client = new S_Client();
						
						ServerSocketChannel channel = (ServerSocketChannel)key.channel();
						//Socket socket = ss.accept();
						//ServerSocketChannel channel = ((ServerSocketChannel) key.channel());
						SocketChannel socketChannel = channel.accept();
						Socket socket= socketChannel.socket();						
						
						client.setClientSocket(socket);
						System.out.print("Got connection from "
								+ client.getClientSocket()+"\n");
				
						client.setState(S_ClientState.ACCEPTED);
						// Make sure to make it non-blocking, so we can
						// use a selector on it.
						SocketChannel sc = client.getClientSocket().getChannel();
						sc.configureBlocking(false);
						clients.put(socket, client);						
						
						// Register it with the selector, for reading
						sc.register(selector, SelectionKey.OP_READ);
					} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {

						SocketChannel sc = null;

						try {

							// It's incoming data on a connection, so
							// process it
							sc = (SocketChannel) key.channel();
							boolean ok = processInput(sc);

							// If the connection is dead, then remove it
							// from the selector and close it
							if (!ok) {
								System.out
								.println("Client Connection Lost");
								throw new IOException("Connection Lost");

							}

						} catch (IOException ie) {

							// On exception, remove this channel from the
							// selector
							key.cancel();
							Socket socket = sc.socket();							
							S_Client client = clients.get(socket);
							disconnect(client);
						}
					}
				}

				// We remove the selected keys, because we've dealt
				// with them.
				keys.clear();
			}
		} catch (IOException ie) {
			System.err.println(ie);
		}
	}

	private void CheckOutbound() {

		S_PacketQueueItem packet;
		while ((packet  = queue.poll())!= null) {
			S_Client client = packet.getClient();			
			if (client == null) {
				return;
			}
			S_PerformanceStats.getInstance().sentPacket(
					packet.getData().length());
			buffer.clear();
			buffer.put(packet.getBytes());
			buffer.flip();
			SocketChannel sc = client.getClientSocket().getChannel();
			try {
				System.out.print("Sending to "+client+"\n" + new String(packet.getData()));
				sc.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				disconnect(client);				
			}
		}
		return;
	}

	public void disconnect(S_Client client) {
		
		if(client!=null) {
			Socket socket= client.getClientSocket();
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

	// Do some cheesy encryption on the incoming data,
	// and send it back out
	private boolean processInput(SocketChannel sc) throws IOException {

		buffer.clear();
		sc.read(buffer);
		buffer.flip();
		Socket socket = sc.socket();
		S_Client client = clients.get(socket);
		
		if (client == null) {
			return false;
		}

		// If no data, close the connection
		if (buffer.limit() == 0) {
			return false;

		}
		S_PerformanceStats.getInstance().receivedPacket(buffer.limit());
		byte[] output = new byte[buffer.limit()];
		for (int j = 0; j < buffer.limit(); ++j) {
			output[j] = buffer.get(j);
		}

		System.out.print(client + " sends: \n");

		char[] decOutput = S_Crypt.getInstance().C2Sdecrypt(output);

		System.out.print(new String(decOutput));
		S_Server.getInstance().getPacketParser().Parse(client, decOutput);
		return true;
	}

	public void SendData(S_Client client, String data) {
		Iterator<S_PacketQueueItem> iter = queue.iterator();
		while (iter.hasNext()) {
			S_PacketQueueItem item = iter.next();
			if (item.getClient() == client) {
				item.addData(data);
				return;

			}
		}
		queue.offer(new S_PacketQueueItem(client, data));
	}

	@Override
	public void Start() throws Exception {

		
		selector = Selector.open();	
	}

	public void register(InetSocketAddress address) {

		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannel.socket();
			serverSocket.bind(new InetSocketAddress(address.getPort()));
			serverChannel.configureBlocking(false);
			
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);			
			channels.add(serverChannel);
			
		} catch (Exception e) {
			if (e instanceof BindException) {
				System.out.println("Port " + address.getPort()
						+ " not available. Is the server already running?");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void Stop() {
		System.out.println("net stop");
		
		for(ServerSocketChannel channel : channels){				
			disconnect(clients.get(channel.socket()));			
		}
	}

	@Override
	public void Work() {

		CheckOutbound();
		CheckInbound();
	}
}
