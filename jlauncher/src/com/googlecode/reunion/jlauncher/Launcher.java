package com.googlecode.reunion.jlauncher;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jcommon.ServerList;
import com.googlecode.reunion.jcommon.ServerList.ServerListItem;

public class Launcher {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		S_Parser launcher = new S_Parser();
		S_Parser servers = new S_Parser();
		ServerList serverList = new ServerList();
		try {
			launcher.Parse("Launcher.dta");		
			servers.Parse("Servers.dta");
			
			Iterator<S_ParsedItem> iter = servers.getItemListIterator();
			
			while(iter.hasNext()) {
				S_ParsedItem server = iter.next();
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
