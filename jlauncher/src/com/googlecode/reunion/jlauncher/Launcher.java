package com.googlecode.reunion.jlauncher;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jcommon.ServerList;

public class Launcher {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			Runtime.getRuntime().exec("Game.exe "+version);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
