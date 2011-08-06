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

public abstract class NetworkThread<T extends Connection<?>> extends Thread {

	private Selector selector;
	
	public NetworkThread(InetSocketAddress address) throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(address);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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
	
	public void connect(InetSocketAddress address ) throws IOException{
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		Selector selector = this.selector;
		SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(address);
		key.attach(socketChannel);
	}
	
	public void onConnect(T connection){
				
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				int num = selector.select();
				if(num == 0){
					// we need synchronize here otherwise we might block again before we were able to change the selector
					synchronized(selector){
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
						onAccept(connection);
					}
					if (connectable){
						SocketChannel socketChannel = (SocketChannel)key.channel();
						if(socketChannel.finishConnect()){
							T connection = createConnection(socketChannel);
							onConnect(connection);							
						}else{
							System.out.println("connection "+socketChannel.socket()+" failed");							
						}
					}
					if(readable||writable){
						T connection = (T) key.attachment();
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
