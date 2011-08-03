package com.googlecode.reunion.jreunion.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public abstract class NetworkThread extends Thread {

	private Selector selector;
	public NetworkThread(InetSocketAddress address) throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);
		ServerSocket serverSocket = selectable.socket();
		serverSocket.bind(address);
		selectable.register(selector, SelectionKey.OP_ACCEPT);
	}

	Selector getSelector() {
		return selector;
	}
	
	public abstract void onAccept(Connection connection);
		
	public abstract void onDisconnect(Connection connection);
	
	public abstract Connection createConnection(SocketChannel socketChannel);
	
	@Override
	public void interrupt() {
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
			super.interrupt();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				int num = selector.select();
				if(num == 0){
					// we need synchronize here otherwise we might block again before we were able to change the selector
					synchronized(this){
						continue;
					}
				}
				Set<SelectionKey> keys = selector.selectedKeys();
				for(SelectionKey key: keys){
					
					if(!key.isValid()){
						continue;		
					}
					if (key.isAcceptable()) {
						SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
						Connection connection = createConnection(socketChannel);						
						onAccept(connection);
					} else {						
						Connection connection = (Connection) key.attachment();
						if (key.isWritable()) {
							connection.handleOutput();
						}
						if (key.isReadable()) {
							connection.handleInput();
						}
					}
				}
				keys.clear();
				
			} catch (Exception e) {
				e.printStackTrace();
				if(e instanceof ClosedSelectorException||e instanceof InterruptedException){
					return;
				}
			}			
		}		
	}

}
