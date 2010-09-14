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
		
		JFrame frame = new LauncherFrame();

	}

}
