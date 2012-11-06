package netty;

import io.netty.channel.Channel;

public class Connection {
	private OtherProtocol protocol = new OtherProtocol();
	private Parser parser;
	private Channel channel;

}
