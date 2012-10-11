package org.reunionemu.jreunion.network.protocol;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jreunion.server.ClassFactory;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Reference;

public abstract class Protocol {
	public static Pattern login = Pattern.compile("^(?:\\d+[\\r\\n]+)(?:(?:login|play)[\\r\\n]+)?(?:.+[\\r\\n]+)*$");
	
	private static List<Class<?>> protocols = new Vector<Class<?>>();
	
	
	public Protocol (){
		
	}	
	
	public static boolean testLogin(String input) {
		
		System.out.println(input);
		Matcher matcher = login.matcher(input);
		return matcher.matches();
		
	}
	
	public static Protocol find(Client client, byte [] data) {
		
		for(Class<?> protocolClass : protocols){
			Protocol protocol = (Protocol) ClassFactory.create(protocolClass, client);
			List<String> decrypted = protocol.decryptServer(data.clone());
			if(decrypted.size()>0 && testLogin(decrypted.get(0))){
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
	
	public abstract List<String> decryptServer(byte data[]);

	public abstract byte[] encryptServer(List<String> data);
	
	//Optional	
	public List<String> decryptClient(byte [] data) {
		throw new UnsupportedOperationException();
	}
	
	//Optional
	public byte[] encryptClient(String data) {
		throw new UnsupportedOperationException();
	}
	
	
}
