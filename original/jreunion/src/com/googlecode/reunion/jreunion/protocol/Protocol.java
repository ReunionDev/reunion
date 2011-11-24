package com.googlecode.reunion.jreunion.protocol;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.ClassFactory;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Reference;

public abstract class Protocol {
	public static Pattern login = Pattern.compile("(\\d+)\\n((login|play)\\n)?((.+)\\n)?((.+)\\n)?");
	
	private static List<Class<?>> protocols = new Vector<Class<?>>();
	
	
	private Client client;
	public Protocol (Client client){
		
		this.client = client;
		
	}
	public Client getClient(){
		return client;
	}
	
	public static boolean testLogin(String input) {
		
		System.out.println(input);
		Matcher matcher = login.matcher(input);
		return matcher.matches();
		
	}
	
	public static Protocol find(Client client, byte [] data) {
		
		for(Class<?> protocolClass : protocols){
			Protocol protocol = (Protocol) ClassFactory.create(protocolClass, client);
			String decrypted = protocol.decryptServer(data.clone());
			if(testLogin(decrypted)){
				 return (Protocol) ClassFactory.create(protocol.getClass(), client);
			}			
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		String input = "1111\nlogin\njake\ntest\n";

		System.out.println(testLogin(input));
	}
	
	
	public static void load() throws ClassNotFoundException{
		
		protocols.clear();
		Parser protocolConfig = new Parser();
		try {
			protocolConfig.Parse("config/Protocols.dta");
			
			Iterator<ParsedItem> iter = protocolConfig.getItemListIterator();
			
			while(iter.hasNext()){
				
				ParsedItem item = iter.next();
				Class<?> protocolName = Class.forName(item.getMemberValue("Class"));
				protocols.add(protocolName);				
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}
	
	public abstract String decryptServer(byte data[]);

	public abstract byte[] encryptServer(String data);
	
	//Optional	
	public String decryptClient(byte [] data) {
		throw new UnsupportedOperationException();
	}
	
	//Optional
	public byte[] encryptClient(String data) {
		throw new UnsupportedOperationException();
	}
	
	public String toString(){
		return this instanceof OtherProtocol ? "OtherProtocol" : "DefaultProtocol";
	}
	
}
