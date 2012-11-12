package netty;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.old.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class PacketParserCodec extends MessageToMessageCodec<String, Packet, Packet, String> {
	ParserFactory parserFactory;
	PacketFactory packetFactory;
	public PacketParserCodec(ParserFactory parserFactory, PacketFactory packetFactory){
		this.parserFactory = parserFactory;
		this.packetFactory = packetFactory;
		
	}
	
	@Override
	public String encode(ChannelHandlerContext ctx, Packet msg)
			throws Exception {		
		return packetFactory.build(msg); 
		
	}

	@Override
	public Packet decode(ChannelHandlerContext ctx, String msg)
			throws Exception {
		Parser parser = parserFactory.getParser(ctx.channel());		
		return parser.parse(msg);
		
	}
	

}
