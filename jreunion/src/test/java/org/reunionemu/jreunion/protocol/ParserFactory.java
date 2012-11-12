package org.reunionemu.jreunion.protocol;

import io.netty.channel.Channel;

public interface ParserFactory {
	
	Parser getParser(Channel channel);

}
