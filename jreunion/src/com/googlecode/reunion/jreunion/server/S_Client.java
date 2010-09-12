package com.googlecode.reunion.jreunion.server;

import java.net.Socket;

import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.server.S_Enums.S_ClientState;
import com.googlecode.reunion.jreunion.server.S_Enums.S_LoginType;
import com.googlecode.reunion.jreunion.server.S_PacketFactory.S_PacketType;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Client {
	
	
	
	
	private String username;

	private String password;

	private Socket clientSocket;

	private int accountId;

	private S_ClientState clientState;

	private G_Player player;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public S_ClientState getClientState() {
		return clientState;
	}

	public void setClientState(S_ClientState clientState) {
		this.clientState = clientState;
	}

	public G_Player getPlayer() {
		return player;
	}

	public void setPlayer(G_Player player) {
		this.player = player;
	}

	public int getCharacterId() {
		return characterId;
	}

	public void setCharacterId(int characterId) {
		this.characterId = characterId;
	}
	public int characterId;
	
	public S_LoginType loginType;

	public S_LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(S_LoginType loginType) {
		this.loginType = loginType;
	}

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
		if(player!=null)
			value+="("+player.getName()+")";
		
		return value;
	}
	

}
