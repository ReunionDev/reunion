package org.reunionemu.jreunion.protocol;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Protocol {
public static Pattern login = Pattern.compile("^(\\d+[\\r\\n]+)((login|play)[\\r\\n]+)?(.+[\\r\\n]+)?$");
	
	private static List<Class<?>> protocols = new Vector<Class<?>>();
	
	private static Logger logger = LoggerFactory.getLogger(Protocol.class);
	
	
	public static boolean testLogin(String input) {
		
		logger.info("testing protocol for:\n%s", input);
		Matcher matcher = login.matcher(input);
		return matcher.matches();
		
	}
	private Client client;
	public Protocol (Client client){
		
		this.client = client;
		
	}
	public Client getClient(){
		return client;
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
