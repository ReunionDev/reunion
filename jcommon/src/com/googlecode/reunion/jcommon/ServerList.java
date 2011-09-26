package com.googlecode.reunion.jcommon;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

public class ServerList 
{
	private List<ServerListItem> items = new Vector<ServerListItem>();
	public void Save(String filename) throws IOException{		
		File file = new File(filename);
		RandomAccessFile output = new RandomAccessFile(file, "rw");
		int e0,d4,a4;
		byte fb,ef;	   
	    output.write(new byte[6]);
	    e0=30779;
	    fb=75;
	    d4=e0+getItems().size();	    
	    output.write(getBytes((short)e0));
	    output.write(new byte[2]);
	    output.writeByte(fb);
	    output.write(getBytes((short)d4));
	    output.write(new byte[2]);
	    for(ServerListItem item:getItems()) {
	    	String line = item.getName()+" "+item.getAddress().getHostAddress()+" "+item.getPort();
	      a4=line.length();
	      e0=d4;
	      d4=a4+e0;
	      output.write(getBytes((short)d4));	      
	      output.write(new byte[2]);
	      for (int j=0;j< a4;j++) {
	        ef=(byte) (((byte)line.toCharArray()[j])+fb);
	        output.writeByte(ef);
	        fb=ef;
	      }
	    }
	  output.close();
		 
	}
	static byte[] getBytes(short input){
		
	    byte high = (byte)(input >>> 8);
	    byte low = (byte)input;
	    return new byte[] {low, high};		
	}
	
	public void Load(String filename) throws IOException{
		getItems().clear();
		
		File file = new File(filename);
		RandomAccessFile input = new RandomAccessFile(file,"r");
		int e0,ef;
	    int d4;
	    String line;
	    input.seek(6);
	    e0 = input.readByte();
	    input.seek(10);
	    int last = input.readByte();
	    input.seek(11);
	    d4 = input.readByte();
	    input.skipBytes(3);
	  	int lines=d4-e0;
	  	for (int i=0; i< lines; i++) {		
		    e0=d4;
		    line="";
		    d4 = input.readByte();
		    input.skipBytes(3);
		    int lineLength=(int) ((byte) (d4-e0)%256);
		    for (int j=0; j<lineLength;j++){
		    	ef =input.readByte();
		    	line+=(char)((byte)(ef-last)%256);
		    	last=ef;
		    }
		    
		    String [] parsed = line.split(" ");
		    
		    getItems().add(new ServerListItem(parsed[0],InetAddress.getByName(parsed[1]),Integer.parseInt(parsed[2])));		    
	  	}
	  	input.close();
		
	}
	
	/**
	 * @return the items
	 */
	public List<ServerListItem> getItems() {
		return items;
	}

	public static class ServerListItem {
		public ServerListItem(String name, InetAddress address, int port) {
			this.address = address;
			this.port = port;
			this.name = name;
		}
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
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private InetAddress address;
		private int port;
		private String name;
	}
}
