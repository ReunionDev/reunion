package com.googlecode.reunion.jreunion.events;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.googlecode.reunion.jreunion.events.client.ClientEvent.ClientFilter;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.Position;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Test extends EventBroadcaster implements EventListener,Runnable {

	public Test() {
		this.addEventListener(Event.class,this, null);
		
		
	}
	
	// Returns a bitset containing the values in bytes.
	// The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
	public static BitSet fromByteArray(byte[] bytes) {
	    BitSet bits = new BitSet();
	    for (int i=0; i<bytes.length*8; i++) {
	        if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	            bits.set(i);
	        }
	    }
	    return bits;
	}

	// Returns a byte array of at least length 1.
	// The most significant bit in the result is guaranteed not to be a 1
	// (since BitSet does not support sign extension).
	// The byte-ordering of the result is big-endian which means the most significant bit is in element 0.
	// The bit at index 0 of the bit set is assumed to be the least significant bit.
	public static byte[] toByteArray(BitSet bits) {
	    byte[] bytes = new byte[bits.length()/8+1];
	    for (int i=0; i<bits.length(); i++) {
	        if (bits.get(i)) {
	            bytes[bytes.length-i/8-1] |= 1<<(i%8);
	        }
	    }
	    return bytes;
	}
	static int count = 0;
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		Test t = new Test();
		String username = "Wfpw";
		byte key = 0x03;
		byte [] input = username.getBytes();
		byte [] output = new byte[input.length];
		for(int i = 0; i<input.length;i++){
			output[i]= (byte) ((byte) (input[i]^key)%256);
			
		}
		
		username = new String(output);
		System.out.println(username);
		
		/*
		int port = 4009;
		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress(port));	
		Thread thread = new Thread(t);
		thread.start();
		Socket cl = socket.accept();
		System.out.println(cl.getLocalSocketAddress());		
		*/
		
		{
			long start = System.currentTimeMillis();
			for(int i =0;i<10000;i++){
				t.fireEvent(t.createEvent(TestEvent.class));
			}
			System.out.println(System.currentTimeMillis()-start);
		}
		System.out.println(count);
		
		EventBroadcaster.shutdown();
		System.out.println(count);
	}

	@Override
	public void handleEvent(Event event) {
		//System.out.println("ohai!"+event.getClass());
		synchronized(this){
			count++;
		}
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket("home.aidamina.org",4009);
			System.out.println(socket);
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
