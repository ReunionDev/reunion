package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.network.NetworkThread;
import com.googlecode.reunion.jreunion.network.PacketConnection;

public class Proxy {

	private ProxyExternalNetwork externalNetwork;	
	private NetworkThread<? extends PacketConnection<?>> internalNetwork;
	
	private PacketConnection<?> loginServer;
	private List<PacketConnection<?>> gameServers = new LinkedList<PacketConnection<?>>();
	
	public Proxy(InetSocketAddress externalAddress, InetSocketAddress externalBoundAddress, InetSocketAddress internal) throws IOException {
		externalNetwork = new ProxyExternalNetwork(externalAddress, externalBoundAddress);
		internalNetwork = new ProxyInternalNetwork(internal);
		
		externalNetwork.start();
		internalNetwork.start();
	}
	public Proxy(InetSocketAddress externalAddress,InetSocketAddress internal) throws IOException {
		this(externalAddress, null, internal);
	}
	
	public void connectToGameServers(){
		Parser maps = new Parser();
		Iterator<ParsedItem> mapsIter = maps.getItemListIterator();
		try {
			maps.Parse("config/Maps.dta");
		} catch (IOException e) {
			e.printStackTrace();
		}
		map_for:
		while(mapsIter.hasNext()){
			ParsedItem item = (ParsedItem)mapsIter.next();
		//for(ParsedItem item: maps){
			try {
				String ip = item.getMemberValue("Ip");
				int port = Integer.parseInt(item.getMemberValue("Port"));
				InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(ip), port);
				for(PacketConnection<?> connection: gameServers){
					if(connection.getSocketChannel().socket()!=null&&connection.getSocketChannel().socket().isConnected()){
						if(connection.getSocketChannel().socket().getRemoteSocketAddress().equals(address))
							continue map_for;
					}
				}
				gameServers.add(internalNetwork.connect(address));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void connectToLoginServer(){
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Logger logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new PatternLayout("%-5p [%t]: %m\r\n"){
			@Override
			public String format(LoggingEvent event) {
				String result = super.format(event);
				if(result.endsWith("\n\r\n")){
					result = result.substring(0, result.length()-2);
				}
				return result;
			}
		}, ConsoleAppender.SYSTEM_OUT));
		
		Proxy proxy = new Proxy(new InetSocketAddress(InetAddress.getByName("192.168.1.199"), 4005), new InetSocketAddress(4005), new InetSocketAddress(5001));
		
		Thread.sleep(1000);
		
		proxy.connectToGameServers();
		//Connection<ProxyExternalConnection> connection = proxy.connect(new InetSocketAddress(4005));
		
		//System.out.println(connection.getSocketChannel());
			
		
	}

}
