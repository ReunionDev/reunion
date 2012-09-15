package org.reunionemu.jlauncher;

import java.io.File;
import java.net.InetAddress;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;
import org.reunionemu.jcommon.ServerList;
import org.reunionemu.jcommon.ServerList.ServerListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
	
	static Logger logger = LoggerFactory.getLogger(Launcher.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Parser launcher = new Parser();
		Parser servers = new Parser();
		ServerList serverList = new ServerList();
		try {
			launcher.Parse("Launcher.dta");		
			servers.Parse("Servers.dta");
			Iterator<ParsedItem> iter = servers.getItemListIterator();
			while(iter.hasNext()) {
				ParsedItem server = iter.next();
				serverList.getItems().add(new ServerListItem(server.getName(), InetAddress.getByName(server.getMemberValue("Address")), Integer.parseInt(server.getMemberValue("Port"))));				
			}
			serverList.Save("SvrList.dta");
			
			String dir = ".";
			dir = "E:\\Games\\BiosFear\\";
			
			String version = launcher.getItem("Launcher").getMemberValue("Version");
			ProcessBuilder builder = new ProcessBuilder(new String[] { "cmd.exe", "/C", "Game.exe "+version });
			builder.directory(new File(dir));
			
			Process p = builder.start();
			//Runtime.getRuntime().exec("E:\\Games\\BiosFear\\game.exe 100");
			//System.out.println(p.waitFor());
			
			
		} catch (Exception e) {
			logger.warn("Exception",e);
		}

	}

}
