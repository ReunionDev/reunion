package com.googlecode.reunion.jreunion.server.protocol;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.ClassFactory;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Reference;

public abstract class Protocol {
	public static Pattern login = Pattern.compile("(\\d+)\\n(login|play)\\n(.+)\\n(.+)\\n");
	
	private static List<Protocol> protocols = new Vector<Protocol>();
	
	public static boolean testLogin(String input) {
		
		System.out.println(input);
		
		Matcher matcher = login.matcher(input);
		
		return matcher.matches();
		
	}
	
	public static Protocol find(Client client, byte [] data) {
		
		for(Protocol protocol : protocols){
			
			String decrypted = protocol.decrypt(client, data.clone());
			
			
			if(testLogin(decrypted)){
				return protocol;
			}			
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		
		
		String input = "1111\nlogin\njake\ntest\n";

		System.out.println(testLogin(input));
	}
	
	
	public static void load(){
		
		protocols.clear();
		Parser protocolConfig = new Parser();
		try {
			protocolConfig.Parse("config/Protocols.dta");
			
			Iterator<ParsedItem> iter = protocolConfig.getItemListIterator();
			
			while(iter.hasNext()){
				
				ParsedItem item = iter.next();
				Protocol protocol = (Protocol) ClassFactory.create(item.getMemberValue("Class"));
				protocols.add(protocol);
				
			}
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}
	
	public abstract String decrypt(Client client, byte data[]);

	public abstract byte[] encrypt(Client client, String data);
	
	
}
