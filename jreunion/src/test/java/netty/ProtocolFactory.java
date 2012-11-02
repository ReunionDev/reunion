package netty;

import io.netty.channel.Channel;

public interface ProtocolFactory {
	
	Protocol getProtocol(Channel channel);

}
