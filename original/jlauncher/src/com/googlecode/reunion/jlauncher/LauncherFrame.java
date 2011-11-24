package com.googlecode.reunion.jlauncher;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;


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
