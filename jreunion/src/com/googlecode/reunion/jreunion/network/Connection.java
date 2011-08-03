package com.googlecode.reunion.jreunion.network;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.Queue;

import com.sun.org.apache.bcel.internal.generic.NEW;

public abstract class Connection {
	
	private ByteBuffer inputBuffer;
	private ByteBuffer outputBuffer;	
	private SocketChannel socketChannel;	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	private NetworkThread networkThread;	

	public Connection(NetworkThread networkThread, SocketChannel socketChannel) throws IOException {
		inputBuffer = ByteBuffer.allocate(1024);
		outputBuffer = ByteBuffer.allocate(1024);
		this.networkThread = networkThread;
		this.socketChannel = socketChannel;
		Selector selector = networkThread.getSelector();
		socketChannel.configureBlocking(false);
		SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
		key.attach(this);
	}
	
	public void write(byte [] data){
		Selector selector = networkThread.getSelector();
		try {
			synchronized(outputBuffer){
				outputBuffer.put(data);
				SelectionKey key = socketChannel.register(selector, SelectionKey.OP_WRITE);
				key.attach(this);	
			}
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			handleDisconnect();
		}		
	}
	
	void handleDisconnect(){
		SelectionKey key = socketChannel.keyFor(networkThread.getSelector());	
		key.cancel();
		networkThread.onDisconnect(this);
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
	
	public abstract void onData(ByteBuffer inputBuffer);
	
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
			e.printStackTrace();
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
			}
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
			key.attach(this);
		} catch (Exception e) {			
			e.printStackTrace();
			handleDisconnect();		
		}
	}
}
