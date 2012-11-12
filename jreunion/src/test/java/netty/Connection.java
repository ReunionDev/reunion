package netty;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

import org.reunionemu.jreunion.protocol.packets.client.LoginPacket;
import org.slf4j.*;

public class Connection {
	
	private static final Logger logger = LoggerFactory.getLogger(Connection.class);
	public Connection(Channel channel){
		parser = new LoginParser(){
			
			@Override
			public Packet parse(String input) {
				Packet packet = super.parse(input);
				if(packet !=null && packet instanceof LoginPacket){
					LoginPacket loginPacket = (LoginPacket)packet;
					encryption.setVersion(loginPacket.getVersion());
					logger.debug("Set version: {}", loginPacket.getVersion());
				}
				return packet;
			}
			
		};
		encryption = new OtherProtocol();
		
		InetSocketAddress address = (InetSocketAddress)channel.localAddress();
		encryption.setAddress(address.getAddress());
		encryption.setMapId(4);
		encryption.setPort(address.getPort());
		
		protocol = new Protocol() {
			
			@Override
			public byte encode(char c) {
				return encryption.encryptServer(c);
			}
			
			@Override
			public char decode(byte b) {
				return encryption.decryptServer(b);
			}
		};
		this.channel = channel;
	}
	private OtherProtocol encryption;
	private Protocol protocol;
	private Parser parser;
	private Channel channel;
	public Parser getParser() {
		return parser;
	}
	public Protocol getProtocol() {
		return protocol;
	}

}
