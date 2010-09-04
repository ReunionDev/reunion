package com.googlecode.reunion.jreunion.server;

import java.net.Socket;

import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.server.S_Enums.S_ClientState;
import com.googlecode.reunion.jreunion.server.S_PacketFactory.S_PacketType;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Client {
	
	
	
	
	public String username;

	public String password;

	public Socket clientSocket;

	public int accountId;

	private S_ClientState clientState;

	public G_Player playerObject;

	public int characterId;

	public S_Client() {
		super();
		accountId = -1;
		clientState = S_ClientState.DISCONNECTED;
		characterId = -1;

	}
/*
	public void disconnect() {
		S_Server.getInstance().getNetworkModule().Disconnect(S_Server.getInstance().getNetworkModule().getClient(networkId));
		if (clientState >= S_Enums.CS_INGAME) {
			playerObject.logout();
		}

	}*/

	public S_ClientState getState() {
		return clientState;
	}

	public void sendWrongVersion(int clientVersion) {
		
		SendPacket(S_PacketType.VERSION_ERROR,clientVersion);
	}
	
	public void SendData(String data) {
		S_Server.getInstance()
		.getNetworkModule()
		.SendData(this,data);		
	}
	public void SendPacket(S_PacketType packetType, Object ...arg){
		
		SendData(S_PacketFactory.createPacket(packetType, arg));
	}

	public void setState(S_ClientState state) {
		clientState = state;
	}
	public String toString(){
		
		String value = "Client";
		if(clientSocket!=null)
			value+="("+clientSocket+")";
		if(playerObject!=null)
			value+="("+playerObject.getName()+")";
		
		return value;
	}
	

}
