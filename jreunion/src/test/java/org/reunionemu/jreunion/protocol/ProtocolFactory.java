package org.reunionemu.jreunion.protocol;


import io.netty.channel.Channel;

public interface ProtocolFactory {
	
	Protocol getProtocol(Channel channel);

}
