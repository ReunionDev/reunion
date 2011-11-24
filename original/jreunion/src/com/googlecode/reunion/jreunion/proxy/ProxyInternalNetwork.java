package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.googlecode.reunion.jreunion.network.NetworkThread;
import com.googlecode.reunion.jreunion.network.PacketConnection;
import com.googlecode.reunion.jreunion.network.PacketServer;

public class ProxyInternalNetwork extends PacketServer<ProxyInternalNetwork.ProxyInternalNetworkConnection> {

	public ProxyInternalNetwork(InetSocketAddress internal) throws IOException {
		super();
		bind(internal);
	}

	@Override
	public ProxyInternalNetworkConnection createConnection(SocketChannel socketChannel) {
		return new ProxyInternalNetworkConnection(this, socketChannel);
	}
	
	public class ProxyInternalNetworkConnection extends PacketConnection<ProxyInternalNetworkConnection>{

		public ProxyInternalNetworkConnection(
				NetworkThread<ProxyInternalNetworkConnection> networkThread,
				SocketChannel socketChannel) {
			super(networkThread, socketChannel);
		}

		@Override
		public void onPacket(Serializable obj) {
			
		}
	}
	
	@Override
	public void onConnect(ProxyInternalNetworkConnection connection) {
	
	}
	@Override
	public void onDisconnect(ProxyInternalNetworkConnection connection) {
		
	}
	
	@Override
	public void onAccept(ProxyInternalNetworkConnection connection) {
		
	}
	
}
