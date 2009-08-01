package com.googlecode.reunion.jreunion.server;


import java.net.Socket;

import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Client {
	public String username;

	public String password;

	public Socket clientSocket;

	public int networkId;

	public int accountId;

	private int clientState;

	public G_Player playerObject;
	
	public int characterId;

	public S_Client() {
		super();
		accountId = -1;
		networkId = -1;
		clientState = -1;
		characterId = -1;
		
	}
	public void sendWrongVersion(int clientVersion)
	{
		S_Server.getInstance().getNetworkModule().SendPacket(networkId,S_PacketFactory.createPacket(S_PacketFactory.PT_VERSION_ERROR,clientVersion));
		return;
	}
	public int getState()
	{
		return clientState;
	}
	public void setState(int state)
	{
		clientState=state;
	}
	public void disconnect()
	{
		S_Server.getInstance().getNetworkModule().Disconnect(networkId);
		if (clientState >= S_Enums.CS_INGAME)
			playerObject.logout();
		
	}

}
