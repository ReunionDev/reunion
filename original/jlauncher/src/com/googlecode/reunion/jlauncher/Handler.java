package com.googlecode.reunion.jlauncher;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends java.net.URLStreamHandler {
	final int DEFAULT_PORT = 4005;
	
	protected java.net.URLConnection openConnection(java.net.URL url)   throws java.io.IOException {		
		return new URLConnection(url) {			
			private java.net.Socket socket;
			
			@Override
			public void connect() throws IOException {
				URL url = getURL();
		        socket = new Socket(url.getHost(), url.getPort()==-1?url.getDefaultPort():url.getPort());
		        connected = true;
			}
			
			public java.io.InputStream getInputStream() throws java.io.IOException {
		        if (!connected)
		        	connect();
		        return socket.getInputStream();
			}
		};
		
  }
	@Override
	protected int getDefaultPort(){
		
		return DEFAULT_PORT;
	}
	
	public static void register() {
		
		final String packageName =
			Handler.class.getPackage().getName();
	    final String pkg = packageName.substring(0, packageName.lastIndexOf( '.' ) );
	    final String protocolPathProp = "java.protocol.handler.pkgs";

	    String uriHandlers = System.getProperty(protocolPathProp, "");
	    if ( uriHandlers.indexOf( pkg ) == -1 ) {
	    	if ( uriHandlers.length() != 0 )
	    		uriHandlers += "|";
	    	uriHandlers += pkg;
	    	System.setProperty( protocolPathProp,uriHandlers );
	    }
	} 
}