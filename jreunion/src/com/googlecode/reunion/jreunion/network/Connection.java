package com.googlecode.reunion.jreunion.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class Connection<T extends Connection<T>> {	
	
	private ByteBuffer inputBuffer;
	private ByteBuffer outputBuffer;	
	private SocketChannel socketChannel;
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	private NetworkThread<T> networkThread;	

	public Connection(NetworkThread<T> networkThread, SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.networkThread = networkThread;
	}
	
	public void open() throws IOException{
		
		inputBuffer = ByteBuffer.allocate(1024);
		outputBuffer = ByteBuffer.allocate(1024);
		Selector selector = networkThread.getSelector();
		socketChannel.configureBlocking(false);
		synchronized(this.networkThread){
			selector.wakeup();
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);		
			key.attach(this);
		}
	}
	
	public void close(){
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
			handleDisconnect();
		}
	}

	public NetworkThread<?> getNetworkThread() {
		return networkThread;
	}

	public void write(byte [] data){
		Selector selector = networkThread.getSelector();
		try {
			synchronized(outputBuffer){
				outputBuffer.put(data);
				synchronized(this.networkThread){
					selector.wakeup();
					SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
					key.attach(this);
				}
			}
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			handleDisconnect();
		}		
	}
	
	void handleDisconnect(){
		SelectionKey key = socketChannel.keyFor(networkThread.getSelector());	
		key.cancel();
		networkThread.onDisconnect((T)this);
	}
	
	public byte [] getData(){
		synchronized(inputBuffer){
			inputBuffer.flip();
			byte [] data = new byte[inputBuffer.limit()];			
			inputBuffer.get(data);
			inputBuffer.clear();
			return data;
		}
	}
	
	public void onData(ByteBuffer inputBuffer){
		throw new UnsupportedOperationException();
	}
	
	void handleInput() {
		try {
			int size = 0;
			synchronized(inputBuffer){
				size = socketChannel.read(inputBuffer);
			}
			if(size==-1){
				handleDisconnect();
			} else {
				synchronized(inputBuffer){
					onData(inputBuffer);
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
			handleDisconnect();
		}
	}

	void handleOutput() {
		Selector selector = networkThread.getSelector();
		try {
			synchronized(outputBuffer){
				outputBuffer.flip();
				socketChannel.write(outputBuffer);
				outputBuffer.clear();
				synchronized(this.networkThread){
					selector.wakeup();
					SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
					key.attach(this);
				}
			}			
		} catch (Exception e) {			
			e.printStackTrace();
			handleDisconnect();		
		}
	}
}
