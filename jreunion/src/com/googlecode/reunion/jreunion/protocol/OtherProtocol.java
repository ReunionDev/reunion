package com.googlecode.reunion.jreunion.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;

public class OtherProtocol extends Protocol {

	
	public static Pattern packetRegex = Pattern.compile("(.+)\\n$");
	public static Pattern placeRegex = Pattern.compile("place (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)\\n");
	public static Pattern walkRegex = Pattern.compile("walk (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)\\n");
	int [] place = new int [6];
	int iter = -1;
	int iterCheck = -1;
	
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
			
		String result = decryptServer(data,iter,iterCheck);		
		if(result.contains("place")){
			
			Matcher matcher = placeRegex.matcher(result);
			while(matcher.find()){
				
				for(int i=0;i<6;i++){
					place[i]= Integer.parseInt(matcher.group(i+1));
				}
			}
		}
		if(result.contains("walk")){
			
			Matcher matcher = placeRegex.matcher(result);
			while(matcher.find()){
				
				for(int i=0;i<4;i++){
					place[i]= Integer.parseInt(matcher.group(i+1));
				}
			}
		}
		
		if(result.contains("encrypt_key")){
			
			int magic0 = magic(address,0);
			int magic1 = magic(address,1);
			
			int magicx = Math.abs(place[0]+place[1]-magic0);
			//int v17 = magic1 + place[0] + v22 - *(_DWORD *)a4;
			//int magicx = (HIDWORD(v17) ^ v17) - HIDWORD(v17);			
			
			
			iter =  magicx%4;
			iterCheck += ( magic1 ^ (magicx + 2 * magic1 - mapId));
			System.out.println("magicx: "+magicx+" "+iter);
		}
		
		return result;
	}
	
	
	public String decryptServer(byte[] data, int iter, int iterCheck) {
		String result = null;
		int magic0 = magic(address, 0);
		int magic1 = magic(address, 1);
		switch(iter+1){
			case 0:
				int magic3 = (port - 17) % 131;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((magic0 ^ data[i]) + 49) ^ magic3);
				}
				result = new String(data);
				break;
			case 1:
				int magic4 = iterCheck - version + 10;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(data[i]^magic4^version);
				}
				result = new String(data);
				break;
			case 2:
				int magic5 = iterCheck +magic1-magic0;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^magic5)-19)^mapId);
				}
				result = new String(data);
				break;
			case 3:
				int magic6 = port + 3 * mapId + mapId % 3;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^magic6)^iterCheck)+4);
				}
				result = new String(data);
				break;
			case 4:
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^(iterCheck+111))+33)^version);
				}
				result = new String(data);
				break;
		}
		if(result==null)
			throw new RuntimeException("Unable to Decrypt");
		return result;
	}
	 
	private int findIter(byte[] data) {
		
		int magic0 = magic(address, 1);
		int magic1 = magic(address, 1);
		System.out.println("findIter!");
		int [] place = new int[]{0,0,0,0};
		int magicx = Math.abs(place[0]+place[1]-magic0);
		iter =  magicx%4;
		iterCheck += ( magic1 ^ (magicx + 2 * magic1 - mapId));
		
		String packet = decryptServer(data, iter, iterCheck);
		if(packetRegex.matcher(packet).matches()){
			return iter;
		}
		/*
		for(int i=0; i<5; i++) {
			int iterCheck = (this.iterCheck)+( magic1 ^ (i + 2 * magic1 - mapId));
			String packet = decryptServer(data, i, iterCheck);
			System.out.println(packet);
			if(packetRegex.matcher(packet).matches()){
				return i;
			}
			
		}
		*/
		return -1;
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
	
	public static void main(String[] args) {
		
		int [] place = new int[6];
		String result = "place 6973 5281 106 -10650 14 1\n";
		if(result.contains("place")){
			Matcher matcher = placeRegex.matcher(result);
			while(matcher.find()){
				
				for(int i=0;i<6;i++){
					place[i]= Integer.parseInt(matcher.group(i+1));
				}
			}
		}
	}
}
