package com.googlecode.reunion.jlauncher;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.googlecode.reunion.jcommon.ServerList;
import com.googlecode.reunion.jcommon.ServerList.ServerListItem;


public class LauncherFrame extends JFrame {
	
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
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ServerList serverList = new ServerList();
		try {
			
			InetAddress.getByName("localhost");
			 S_Parser parser = new  S_Parser();
			//parser.Parse("launcher.dta");
			serverList.getItems().add(serverList.new ServerListItem("test", InetAddress.getByName("127.0.0.1"), 1234));
			serverList.Save("SvrList.dta");
			
			serverList.Load("C:\\Users\\Aidamina\\Documents\\SvrList.dta");
			
			RandomAccessFile r1 = new RandomAccessFile("test.dta", "r");
			RandomAccessFile r2 = new RandomAccessFile("test2.dta", "r");
			for(int i =0 ;i<r1.length();i++){
				
				//System.out.println(String.format("%x %x",r1.read(),r2.read()));
				
			}
			
			System.out.println(serverList.getItems().size());
			for(ServerListItem item:serverList.getItems()) {
				System.out.println(item.getName());
				
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		JFrame frame = new LauncherFrame();

	}

}
