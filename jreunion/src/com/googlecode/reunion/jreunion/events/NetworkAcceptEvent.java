package com.googlecode.reunion.jreunion.events;

import java.net.Socket;

import com.googlecode.reunion.jreunion.server.Client;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class NetworkAcceptEvent extends NetworkEvent
{
	public NetworkAcceptEvent(Socket socket) {
		super(socket);
	}

}