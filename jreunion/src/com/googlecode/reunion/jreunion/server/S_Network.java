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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Network extends S_ClassModule {
	Queue<S_PacketQueueItem> queue = new LinkedList<S_PacketQueueItem>();

	private final ByteBuffer buffer = ByteBuffer.allocate(16384);

	List<S_Client> clientList = new Vector<S_Client>();

	private ServerSocketChannel serverChannel;

	private Selector selector;

	private ServerSocket ss;

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
					break;// continue;
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

						// System.out.println( "acc" );
						// It's an incoming connection.
						// Register this socket with the Selector
						// so we can listen for input on it
						S_Client client = new S_Client();

						client.clientSocket = ss.accept();
						System.out.print("Got connection from "
								+ client.clientSocket);
						client.networkId = 0;
						// Start looking for a free networkId;
						int j = 0;
						while (true) {
							for (int i = 0; i < clientList.size(); i++) {
								if (client.networkId == clientList.get(i).networkId) {
									client.networkId++;
								}

							}
							if (client.networkId != j) {
								j++;
							} else {
								break;
							}
						}
						// End looking for a free networkId
						System.out.println(" assigned to id: "
								+ client.networkId);
						client.setState(S_Enums.CS_ACCEPTED);
						// Make sure to make it non-blocking, so we can
						// use a selector on it.
						SocketChannel sc = client.clientSocket.getChannel();
						sc.configureBlocking(false);
						clientList.add(client);
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
								key.cancel();

								try {
									System.out
											.println("Client Connection Lost");

									Socket s = sc.socket();

									Iterator<S_Client> iter = getClientIterator();
									while (it.hasNext()) {

										S_Client client = iter.next();
										if (client.clientSocket == s) {
											System.out
													.println("Disconnecting: Client("
															+ client.networkId
															+ ")");
											client.playerObject.logout();
											clientList.remove(client);
										}

									}
									sc.close();
								} catch (IOException ie) {
									// System.err.println("Error closing socket ");
									// + s + ": " + ie);
								}
							}

						} catch (IOException ie) {

							// On exception, remove this channel from the
							// selector
							key.cancel();

							Socket s = sc.socket();
							Iterator<S_Client> iter = getClientIterator();
							while (it.hasNext()) {
								S_Client client = iter.next();
								if (client.clientSocket == s) {

									System.out.println("Disconnecting: Client("
											+ client.networkId + ")");
									client.playerObject.logout();
									clientList.remove(client);
								}
							}
							try {
								sc.close();
							} catch (IOException ie2) {
								System.out.println(ie2);
							}

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

		S_PacketQueueItem packet = queue.poll();
		while (packet != null) {

			S_Client client = null;
			Iterator<S_Client> iter = getClientIterator();
			while (iter.hasNext()) {
				S_Client tempClient = iter.next();
				if (tempClient.networkId == packet.networkId) {

					client = tempClient;
				}
			}
			if (client == null) {
				return;
			}
			S_PerformanceStats.getInstance().sentPacket(
					packet.getData().length());
			buffer.clear();
			buffer.put(packet.getBytes());
			buffer.flip();
			SocketChannel sc = client.clientSocket.getChannel();
			try {
				System.out.println("Sending (" + new String(packet.getData())
						+ ") to Client(" + client.networkId + ")");
				sc.write(buffer);
			} catch (IOException e) {
				client.disconnect();
				// e.printStackTrace();
			}
			packet = queue.poll();
		}
		return;
	}

	public void Disconnect(int networkId) {

		Iterator<S_Client> iter = getClientIterator();
		while (iter.hasNext()) {
			S_Client client = iter.next();

			if (client.networkId == networkId) {
				try {
					client.clientSocket.close();
				} catch (IOException e) {

					// e.printStackTrace();
				}
				clientList.remove(client);
				if (client.playerObject != null) {
					S_Server.getInstance().getWorldModule().getPlayerManager()
							.removePlayer(client.playerObject);
				}

			}
		}
	}

	public S_Client getClient(G_Player player) {
		Iterator<S_Client> iter = getClientIterator();
		while (iter.hasNext()) {
			S_Client client = iter.next();

			if (client.playerObject == player) {
				return client;
			}
		}

		return null;
	}

	public S_Client getClient(int networkId) {
		Iterator<S_Client> iter = getClientIterator();
		while (iter.hasNext()) {
			S_Client client = iter.next();
			if (client.networkId == networkId) {
				return client;
			}
		}
		return null;
	}

	public Iterator<S_Client> getClientIterator() {
		return clientList.iterator();
	}

	// Do some cheesy encryption on the incoming data,
	// and send it back out
	private boolean processInput(SocketChannel sc) throws IOException {

		buffer.clear();
		sc.read(buffer);
		buffer.flip();
		S_Client client = null;
		Iterator<S_Client> iter = getClientIterator();
		while (iter.hasNext()) {
			S_Client tempClient = iter.next();
			if (tempClient.clientSocket == sc.socket()) {
				client = tempClient;
			}

		}
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

		System.out.print("Client(" + client.networkId + ")" + " sends: \n");

		char[] decOutput = S_Crypt.getInstance().C2Sdecrypt(output);

		System.out.print(new String(decOutput));
		S_Server.getInstance().getPacketParser().Parse(client, decOutput);
		return true;
	}

	public void SendPacket(int networkId, String packetData) {
		Iterator<S_PacketQueueItem> iter = queue.iterator();
		while (iter.hasNext()) {
			S_PacketQueueItem item = iter.next();
			if (item.networkId == networkId) {

				item.addData(packetData);
				return;

			}
		}
		queue.offer(new S_PacketQueueItem(networkId, packetData));

	}

	@Override
	public void Start() throws Exception {

		int port = 4001;
		try {
			serverChannel = ServerSocketChannel.open();
			ss = serverChannel.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			ss.bind(address);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			if (e instanceof BindException) {
				System.out.println("Port " + port
						+ " not available. Is the server already running?");
				throw e;
			}

		}

	}

	@Override
	public void Stop() {
		System.out.println("net stop");
	}

	@Override
	public void Work() {

		CheckOutbound();
		CheckInbound();
	}
}
