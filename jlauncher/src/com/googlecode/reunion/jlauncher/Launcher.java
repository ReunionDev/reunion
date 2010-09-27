package com.googlecode.reunion.jlauncher;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jcommon.ServerList;

public class Launcher {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			
		},ConsoleAppender.SYSTEM_OUT));
		Parser launcher = new Parser();
		Parser servers = new Parser();
		ServerList serverList = new ServerList();
		try {
			launcher.Parse("Launcher.dta");		
			servers.Parse("Servers.dta");
			Iterator<ParsedItem> iter = servers.getItemListIterator();
			while(iter.hasNext()) {
				ParsedItem server = iter.next();
				serverList.getItems().add(serverList.new ServerListItem(server.getName(), InetAddress.getByName(server.getMemberValue("Address")), Integer.parseInt(server.getMemberValue("Port"))));				
			}
			serverList.Save("SvrList.dta");
			String version = launcher.getItem("Launcher").getMemberValue("Version");
			ProcessBuilder builder = new ProcessBuilder(new String[] { "cmd.exe", "/C", "Game.exe "+version });
			builder.directory(new File("."));
			
			builder.start();
			
			
		} catch (IOException e) {
			Logger.getLogger(Launcher.class).warn("Exception",e);
		}

	}

}
