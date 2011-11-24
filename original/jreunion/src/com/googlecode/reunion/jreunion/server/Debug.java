package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.xml.XMLLayout;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;

public class Debug {
	
	public Debug(){
		
		Logger logger = Logger.getRootLogger();

		logger.setLevel(Level.ALL);
		Parser loggerConfigReference = new Parser();
		try {
			loggerConfigReference.Parse("config/Logger.dta");
		} catch (IOException e1) {			
			e1.printStackTrace();
			return;
		}
		
		Iterator<ParsedItem> iter = loggerConfigReference.getItemListIterator();
		while(iter.hasNext()){
			
			ParsedItem item = iter.next();
			if(!item.checkMembers(new String[]{"Type"})){
				Logger.getLogger(Debug.class).warn("Invalid Logger entry");
				continue;
			}
			String type = item.getMemberValue("Type");
			
			AppenderSkeleton appender = null;
			if(type.equalsIgnoreCase("Socket")){
				
				String host = item.getMemberValue("Host");
				InetAddress address = null;
				try {
					address = InetAddress.getByName(host);
				} catch (UnknownHostException e) {
					
					Logger.getLogger(Debug.class).warn("Logging for '"+item.getName()+"' to '"+host+"' disabled (host not found)");
					continue;
				}				
				int port = Integer.parseInt(item.getMemberValue("Port"));
				SocketAppender socketAppender = new SocketAppender(address, port);
				if(item.getMemberValue("Reconnect")!=null){
					socketAppender.setReconnectionDelay(Integer.parseInt(item.getMemberValue("Reconnect")));
				}
				appender = socketAppender;
				
			}else if(type.equalsIgnoreCase("XML")||type.equalsIgnoreCase("HTML")){
				Layout layout = null;
				String path = item.getMemberValue("Path");
				Calendar cal = Calendar.getInstance();
				path = path.replace("%d", ""+cal.get(Calendar.DAY_OF_MONTH));
				path = path.replace("%m", ""+(cal.get(Calendar.MONTH)+1));
				path = path.replace("%y", ""+cal.get(Calendar.YEAR));
				if(type.equalsIgnoreCase("XML")){
					layout = new XMLLayout();
				}else if(type.equalsIgnoreCase("HTML")){
					layout = new HTMLLayout();
				}
				try {
					appender = new FileAppender(layout, path,true);
				} catch (IOException e) {
					Logger.getLogger(this.getClass()).warn("Exception",e);
				}
			}
			if(appender!=null)
				logger.addAppender(appender);
			
			Logger.getLogger(Debug.class).info("Logger " + item.getName() + " started");
		}
	}
}
