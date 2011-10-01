package com.googlecode.reunion.jreunion.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import junit.framework.TestCase;

import org.junit.Test;

public class NetworkTest extends TestCase{

	@Test
	public void testNetwork() throws Exception{
		MockNetworkThread node1 = new MockNetworkThread();
		assertFalse(node1.isAlive());
		
		MockNetworkThread node2 = new MockNetworkThread();
		assertFalse(node2.isAlive());
		
		
		node1.start();
		node2.start();
		Thread.sleep(1000);
		
		assertTrue(node1.isAlive());
		assertTrue(node1.isAlive());
		InetSocketAddress node1Address = new InetSocketAddress(1111);
		node1.bind(node1Address);
		
		try{
			node1.bind(new InetSocketAddress(1111));
			fail();
		}catch(IOException e){
			
		}catch(Exception e){
			fail();
		}
		InetSocketAddress node2Address = new InetSocketAddress(2222);
		node2.bind(node2Address);
		
		node1.connect(node2Address);
				
		node1.interrupt();
		node2.interrupt();
		
		node1.join();
		node2.join();
		
	}
	
	static class MockNetworkThread extends NetworkThread<MockConnection>{

		public MockNetworkThread() throws IOException {
			super();
		}
		
		@Override
		public MockConnection createConnection(SocketChannel socketChannel) {
			return new MockConnection(this, socketChannel);
		}
	}
	static class MockConnection extends Connection<MockConnection>{

		public MockConnection(NetworkThread<MockConnection> networkThread,
				SocketChannel socketChannel) {
			super(networkThread, socketChannel);
		}
		
	}
}
