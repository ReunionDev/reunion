package org.reunionemu.jreunion.server;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.EventListener;
import org.reunionemu.jreunion.events.client.ClientDisconnectEvent;
import org.reunionemu.jreunion.events.client.ClientEvent;
import org.reunionemu.jreunion.events.client.ClientReceiveEvent;
import org.reunionemu.jreunion.events.client.ClientSendEvent;
import org.reunionemu.jreunion.events.network.NetworkDataEvent;
import org.reunionemu.jreunion.events.network.NetworkDisconnectEvent;
import org.reunionemu.jreunion.events.network.NetworkEvent;
import org.reunionemu.jreunion.events.network.NetworkSendEvent;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.protocol.OtherProtocol;
import org.reunionemu.jreunion.protocol.Protocol;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Client extends EventDispatcher implements EventListener, Sendable {
	
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

	private SocketChannel socketChannel;

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

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public Protocol getProtocol(){
		return this.protocol;
	}
	
	private World world;
	
	public World getWorld() {
		return world;
	}
	private void setWorld(World world) {
		this.world = world;
	}

	private void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
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
	
	public Client(World world, SocketChannel socketChannel) {
		
		super();
		setWorld(world);
		accountId = -1;
		state = State.DISCONNECTED;
		setSocketChannel(socketChannel);
		world.addEventListener(ClientSendEvent.class, this, new ClientEvent.ClientFilter(this));
		Server.getInstance().getNetwork().addEventListener(NetworkDisconnectEvent.class, this, new NetworkEvent.NetworkFilter(socketChannel));
	}

	public State getState() {
		return state;
	}

	public void sendWrongVersion(int clientVersion) {
		String requiredVersion = Integer.toString((int)getWorld().getServerSetings().getDefaultVersion());
		String message = "Wrong clientversion: current version "
				+ clientVersion + " required version "
				+ requiredVersion;
		
		sendPacket(PacketFactory.Type.FAIL,message);
	}
	
	public void sendData(String data) {
		//synchronized(this){
			this.outputBuffer.append(data);
			if(!data.endsWith("\n")){
				this.outputBuffer.append("\n");
			}		
		//}
		this.fireEvent(NetworkSendEvent.class, this.getSocketChannel());
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
			buffer.append(Server.logger.isDebugEnabled() ? ", " : "");
		}
		
		if(getState() != State.INGAME || Server.logger.isDebugEnabled()){
			if (socketChannel != null) {
				buffer.append("socket: ");
				// buffer.append(socketChannel);
				buffer.append("local="
						+ this.getSocketChannel().socket()
								.getLocalSocketAddress()
						+ " remote="
						+ this.getSocketChannel().socket()
								.getRemoteSocketAddress());
				buffer.append(", ");
			}

			if (this.getProtocol() != null) {
				buffer.append("encryption level: ");
				buffer.append(getProtocol() instanceof OtherProtocol ? ((OtherProtocol) getProtocol())
						.getEncryptionLevel() : "Default");
				buffer.append(", ");
			}

			buffer.append("state: ");
			buffer.append(getState());
		
		}
		buffer.append("}");
		return buffer.toString();
	}

	public void disconnect() {
		Server.getInstance().getNetwork().disconnect(this.getSocketChannel());		
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
		LOADING,		
		PORTING,
		LOADED,
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
		LoggerFactory.getLogger(Network.class).info("Sending to "+this+":\n" + packetData);
		return protocol.encryptServer(packetData);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof NetworkEvent){
			NetworkEvent networkEvent = (NetworkEvent) event;
			if(event instanceof NetworkDataEvent) {
				//synchronized(this){
					NetworkDataEvent networkDataEvent = (NetworkDataEvent) networkEvent;
					byte [] data = networkDataEvent.getData();
					if(protocol==null){
						protocol = Protocol.find(this, data);
						if(protocol==null) {							
							this.disconnect();
							throw new RuntimeException("Unknown Protocol");//TODO: Proper handling
						}
						LoggerFactory.getLogger(Client.class).info(this + " protocol discovered: "+protocol);
					}
					String decryptedData = protocol.decryptServer(data);
					LoggerFactory.getLogger(Client.class).debug("{}: \n {}", this, decryptedData);
					
					this.inputBuffer.append(decryptedData);
					if(!decryptedData.endsWith("\n"))
						inputBuffer.append("\n");
					fireEvent(ClientReceiveEvent.class, this);	
				//}
			}
			if(event instanceof NetworkDisconnectEvent){				
				
				//LoggerFactory.getLogger(Client.class).debug(event);
				fireEvent(ClientDisconnectEvent.class, this);
			}
		}
	}	
}
