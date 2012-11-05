package netty;

import io.netty.channel.Channel;

public interface ParserFactory {
	
	Parser getParser(Channel channel);

}
