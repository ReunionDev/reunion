package com.googlecode.reunion.jreunion.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public abstract class NetworkThread<T extends Connection<T>> extends Thread {

	private Selector selector;
	
	public NetworkThread() throws IOException {
		this.selector = Selector.open();
	}
	
	public SelectionKey bind(InetSocketAddress address) throws IOException{
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(address);
		synchronized(this){
			selector.wakeup();
			SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);		
			return key;
		}
	}

	Selector getSelector() {
		return selector;
	}
	
	public void onAccept(T connection){
		throw new UnsupportedOperationException();
	}
		
	public void onDisconnect(T connection){
		throw new UnsupportedOperationException();
	}
	
	public abstract T createConnection(SocketChannel socketChannel);
	
	@Override
	public void interrupt() {
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
			super.interrupt();
		}
	}
	
	public T connect(InetSocketAddress address ) throws IOException{
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		T connection = createConnection(socketChannel);
		synchronized(this){
			selector.wakeup();
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT);
			key.attach(connection);
		}
		socketChannel.connect(address);
		return connection;
	}
	
	public void onConnect(T connection){
		throw new UnsupportedOperationException();
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
					boolean connectable = key.isConnectable(), readable = key.isReadable(), writable = key.isWritable(), acceptable = key.isAcceptable();
					if (acceptable) {
						SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
						T connection = createConnection(socketChannel);
						connection.open();
						onAccept(connection);
					}
					if(readable||writable||connectable){
						T connection = (T) key.attachment();
						if(connectable){
							SocketChannel socketChannel = (SocketChannel)key.channel();
							try {
								socketChannel.finishConnect();
							} catch(IOException e) {
								e.printStackTrace();
							}
							connection.open();
							onConnect(connection);
						}
						if (writable) {
							connection.handleOutput();
						}
						if (readable) {
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
