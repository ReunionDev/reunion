package com.googlecode.reunion.jreunion.server;

import java.net.Socket;

import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientDisconnectEvent;
import com.googlecode.reunion.jreunion.events.client.ClientEvent;
import com.googlecode.reunion.jreunion.events.client.ClientReceiveEvent;
import com.googlecode.reunion.jreunion.events.client.ClientSendEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkAcceptEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDataEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkDisconnectEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkEvent;
import com.googlecode.reunion.jreunion.events.network.NetworkSendEvent;
import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Client extends EventBroadcaster implements EventListener {
	
	public StringBuffer getInputBuffer() {
		return inputBuffer;
	}
	public StringBuffer getOutputBuffer() {
		return outputBuffer;
	}
		
	private String username;

	private String password;

	private Socket socket;

	private int accountId;

	private State clientState;

	private Player player;
	
	private StringBuffer inputBuffer = new StringBuffer();
	
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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public LoginType loginType;

	public LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}

	public Client() {
		super();
		accountId = -1;
		clientState = State.DISCONNECTED;
		
		Server.getInstance().getWorldModule().addEventListener(ClientSendEvent.class, this, new ClientEvent.ClientFilter(this));
		Server.getInstance().getNetworkModule().addEventListener(NetworkDisconnectEvent.class, this, new NetworkEvent.NetworkFilter(this.socket));
	}

	public State getState() {
		return clientState;
	}

	public void sendWrongVersion(int clientVersion) {
		
		SendPacket(PacketFactory.Type.VERSION_ERROR,clientVersion);
	}
	
	public void SendData(String data) {
		synchronized(this){
			this.outputBuffer.append(data);
			if(!data.endsWith("\n")){
				this.outputBuffer.append("\n");
			}
			
			this.fireEvent(NetworkSendEvent.class, this.getSocket());
			
		}
		
	}
	public void SendPacket(PacketFactory.Type packetType, Object ...arg){		
		SendData(PacketFactory.createPacket(packetType, arg));
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
		Server.getInstance().getNetworkModule().disconnect(this.getSocket());		
	}
	
	public static enum State {
		
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
	
	public enum LoginType{
		PLAY,
		LOGIN		
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof NetworkEvent){
			Socket socket =((NetworkEvent) event).getSocket();
			if(event instanceof NetworkDataEvent) {
				synchronized(this){
					NetworkDataEvent networkDataEvent = (NetworkDataEvent) event;
					String data = networkDataEvent.getData();
					this.inputBuffer.append(networkDataEvent.getData());
					if(!data.endsWith("\n"))
						inputBuffer.append("\n");
					fireEvent(ClientReceiveEvent.class,this);			
				}
			}
			if(event instanceof NetworkDisconnectEvent){				
				fireEvent(ClientDisconnectEvent.class, this);
			}
		}
	}	
}
