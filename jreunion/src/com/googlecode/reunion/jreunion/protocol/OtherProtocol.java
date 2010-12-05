package com.googlecode.reunion.jreunion.protocol;

import java.net.InetAddress;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;

public class OtherProtocol extends Protocol {

	
	
	public OtherProtocol(Client client) {
		super(client);
		if(client!=null){
			address = getClient().getSocket().getLocalAddress();
			port =  getClient().getSocket().getLocalPort();
			version = getClient().getVersion();
			for(Map map :Server.getInstance().getWorld().getMaps()){
				if(map.getAddress().getAddress().equals(address)&&map.getAddress().getPort()==port) {
					mapId = map.getId();
				}
			}
		}
	}
	
	private InetAddress address;
	private int port = 4005;
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	private int version = 100;
	private int mapId = 4;


	@Override
	public String decryptServer(byte[] data) {
		System.out.println(address+" "+port);
		
		int magic1 = magic(address, 0);
		int magic2 = (port - 17) % 131;
		for(int i=0;i<data.length;i++){
			data[i]=(byte)(((magic1 ^ data[i]) + 49) ^ magic2);
		}		
		return new String(data);
	}
	
	
	@Override
	public String decryptClient(byte[] data){
		
		int magic1 = OtherProtocol.magic(address, 0);
		int magic2 = OtherProtocol.magic(address, 1);
		int magic4 = magic1 - port - mapId + version;
		
		for(int i=0;i<data.length;i++){
			
			int step1 = magic2 ^ data[i];
			int step2 = step1 - 19;			
			int step3 = step2 ^ magic4;			
			data[i] = (byte)step3;
		}		
		return new String(data);
		}
	
	@Override
	public byte[] encryptClient(String data){
		
		
		return null;
	}
	
	@Override
	public byte[] encryptServer(String packet) {
	
		//refresh version because its not always available on connect
		if(getClient()!=null)
			version = getClient().getVersion();
		
		if(mapId==-1) {
			throw new RuntimeException("Invalid Map");
		}
		
		int magic1 = magic(address, 0);
		int magic2 = magic(address, 1);
		int magic4 = magic1 - port - mapId + version;
		
		byte [] data = packet.getBytes();
		
		for(int i = 0; i<data.length; i++) {
			
			int rstep3 = data[i] ^ magic4;
			int rstep2 = rstep3 + 19;
			int rstep1 = magic2 ^ rstep2;
			data[i] = (byte)rstep1;
		}
		return data;
		
		
	}
	
	public static int magic(InetAddress ip, int a2)
	{
		byte [] rip = ip.getAddress();
	    
		if ( a2 == 1 )
			return rip[0] ^ rip[1] ^ rip[2] ^ rip[3];
		else
			return rip[0] + rip[1] + rip[2] + rip[3];
	   
	 }
}
