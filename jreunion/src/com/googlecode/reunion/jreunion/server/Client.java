package com.googlecode.reunion.jreunion.server;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

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
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.protocol.Protocol;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Client extends EventBroadcaster implements EventListener,Sendable {
	
	public StringBuffer getInputBuffer() {
		return inputBuffer;
	}
	public StringBuffer getOutputBuffer() {
		return outputBuffer;
	}
	private int version;
		
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	private String username;

	private String password;
	
	private Protocol protocol;

	private Socket socket;

	private int accountId;

	private State state;

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
	
	private World world;
	
	public World getWorld() {
		return world;
	}
	private void setWorld(World world) {
		this.world = world;
	}

	private void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
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
	
	public Client(World world, Socket socket) {
		
		super();
		setWorld(world);
		accountId = -1;
		state = State.DISCONNECTED;
		setSocket(socket);
		world.addEventListener(ClientSendEvent.class, this, new ClientEvent.ClientFilter(this));
		Server.getInstance().getNetwork().addEventListener(NetworkDisconnectEvent.class, this, new NetworkEvent.NetworkFilter(this.socket));
	}

	public State getState() {
		return state;
	}

	public void sendWrongVersion(int clientVersion) {
		String requiredVersion = Reference.getInstance().getServerReference().getItem("Server").getMemberValue("Version");
		String message = "Wrong clientversion: current version "
				+ clientVersion + " required version "
				+ requiredVersion;
		
		sendPacket(PacketFactory.Type.FAIL,message);
	}
	
	public void sendData(String data) {
		synchronized(this){
			this.outputBuffer.append(data);
			if(!data.endsWith("\n")){
				this.outputBuffer.append("\n");
			}		
		}
		this.fireEvent(NetworkSendEvent.class, this.getSocket());
	}
	
	public void sendPacket(PacketFactory.Type packetType, Object ...args){		
		sendData(PacketFactory.createPacket(packetType, args));
	}

	public void setState(State state) {
		this.state = state;
	}
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		if(player!=null) {
			buffer.append("player: ");
			buffer.append(player);
			buffer.append(", ");
		}
		if(socket!=null){
			buffer.append("socket: ");
			buffer.append(socket);
			buffer.append(", ");
		}
		
				
		buffer.append("state: ");
		buffer.append(getState());
		
		buffer.append("}");
		return buffer.toString();
	}

	public void disconnect() {
		Server.getInstance().getNetwork().disconnect(this.getSocket());		
	}
	
	public static enum State {
		
		DISCONNECTED,	
		ACCEPTED,	
		GOT_VERSION ,	
		GOT_LOGIN ,	
		GOT_USERNAME,	
		GOT_PASSWORD,
		GOT_AUTH,	
		CHAR_LIST,	
		CHAR_SELECTED,		
		PORTING,
		LOADING,
		INGAME;
	}
	
	public enum LoginType{
		PLAY,
		LOGIN		
	}
	
	public byte[] flush(){
		
		StringBuffer outputBuffer = this.getOutputBuffer();
		if(outputBuffer.length()==0)
			return null;
		String packetData = outputBuffer.toString();		
		outputBuffer.setLength(0);
		Logger.getLogger(Network.class).debug("Sending to "+this+":\n" + packetData);
		return protocol.encrypt(this, packetData);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof NetworkEvent){
			Socket socket =((NetworkEvent) event).getSocket();
			if(event instanceof NetworkDataEvent) {
				synchronized(this){
					NetworkDataEvent networkDataEvent = (NetworkDataEvent) event;

					byte [] data = networkDataEvent.getData();
					
					if(protocol==null){
						protocol = Protocol.find(this, data);
						if(protocol==null) {							
							this.disconnect();
							throw new RuntimeException("Unknown Protocol");//TODO: Proper handling
						}
						
						Logger.getLogger(Client.class).debug(this + " protocol discovered: "+protocol);
					}
					
					String decryptedData = protocol.decrypt(this, data);
					Logger.getLogger(Client.class).debug(this + " sends: \n"+decryptedData);
					
					this.inputBuffer.append(decryptedData);
					if(!decryptedData.endsWith("\n"))
						inputBuffer.append("\n");
					fireEvent(ClientReceiveEvent.class,this);			
				}
			}
			if(event instanceof NetworkDisconnectEvent){				
				

				Logger.getLogger(Client.class).debug(event);
				fireEvent(ClientDisconnectEvent.class, this);
			}
		}
	}	
}
