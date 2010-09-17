package com.googlecode.reunion.jreunion.server;

import java.net.Socket;

import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.server.S_Enums.S_LoginType;
import com.googlecode.reunion.jreunion.server.S_PacketFactory.S_PacketType;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Client {
	
	
	
	
	public StringBuffer getOutputBuffer() {
		return outputBuffer;
	}
	private String username;

	private String password;

	private Socket socket;

	private int accountId;

	private State clientState;

	private G_Player player;
	
	private StringBuffer outputBuffer = new StringBuffer();

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

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public State getClientState() {
		return clientState;
	}

	public void setClientState(State clientState) {
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
		clientState = State.DISCONNECTED;
		characterId = -1;

	}

	public State getState() {
		return clientState;
	}

	public void sendWrongVersion(int clientVersion) {
		
		SendPacket(S_PacketType.VERSION_ERROR,clientVersion);
	}
	
	public void SendData(String data) {
		synchronized(this){
			this.outputBuffer.append(data);
			if(!data.endsWith("\n")){
				this.outputBuffer.append("\n");
			}
			
			S_Server.getInstance()
			.getNetworkModule().notifySend(this);
		}
		
	}
	public void SendPacket(S_PacketType packetType, Object ...arg){
		
		SendData(S_PacketFactory.createPacket(packetType, arg));
	}

	public void setState(State state) {
		clientState = state;
	}
	public String toString(){
		
		String value = "Client";
		if(socket!=null)
			value+="("+socket+")";
		if(player!=null)
			value+="("+player.getName()+")";		
		return value;
	}

	public void disconnect() {
		S_Server.getInstance().getNetworkModule().disconnect(this);		
	}
	
	public enum State {
		
		DISCONNECTED(-1),
	
		ACCEPTED( 0),
	
		GOT_VERSION (1),
	
		GOT_LOGIN ( 2),
	
		GOT_USERNAME( 3),
	
		GOT_PASSWORD( 4),
	
		GOT_AUTH( 5),
	
		CHAR_LIST( 6),
	
		CHAR_SELECTED(7),
		
		PORTING(9),
	
		INGAME( 10);
		
		int value;
		
		State(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		
		}
	}
	
}
