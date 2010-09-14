package com.googlecode.reunion.jlauncher;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jcommon.ServerList;
import com.googlecode.reunion.jcommon.ServerList.ServerListItem;


public class LauncherFrame extends JFrame {
	
	S_Parser launcher = new S_Parser();
	S_Parser servers = new S_Parser();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6722429400168968990L;

	public LauncherFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("Hello biosfear!");
		getContentPane().add(label, BorderLayout.CENTER);
		pack();
		setVisible(true);
		
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		JFrame frame = new LauncherFrame();

	}

}
