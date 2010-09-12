package com.googlecode.reunion.jlauncher;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class Handler extends java.net.URLStreamHandler {
	protected java.net.URLConnection openConnection(java.net.URL url)   throws java.io.IOException {
		return new URLConnection(url) {
			
			private java.net.Socket socket;
			
			@Override
			public void connect() throws IOException {
				final String host = getURL().getHost();
		        socket = new java.net.Socket( host, 4005 );
		        connected = true;
				
			}

		      public java.io.InputStream getInputStream()
		      throws java.io.IOException {
		        if ( ! connected )
		          connect();
		        return socket.getInputStream();
		      }
		};
  
     
  }
	
	 public static void register() {
	      final String packageName =
	         Handler.class.getPackage().getName();
	      final String pkg = packageName.substring(
	         0, packageName.lastIndexOf(  '.' ) );
	      final String protocolPathProp =
	         "java.protocol.handler.pkgs";

	      String uriHandlers = System.getProperty(
	         protocolPathProp, "" );
	      if ( uriHandlers.indexOf( pkg ) == -1 ) {
	        if ( uriHandlers.length() != 0 )
	          uriHandlers += "|";
	        uriHandlers += pkg;
	        System.setProperty( protocolPathProp,
	           uriHandlers );
	      }
	 }
	
	 
}