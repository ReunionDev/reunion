package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;


public class ProtocolCodec extends ByteToMessageCodec<String, String> {
	
	public byte encode(char c) {		
		return (byte) ((c ^ 0xc3) + 0x0f);		
	}
	
	public char decode(byte b) {
		return (char)(b - 15);
	}

	@Override
	public String decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		in.markReaderIndex();
		StringBuffer buffer = new StringBuffer();
		int size = in.readableBytes();
		
		for(int i =0; i < size; i++){
			char c = decode(in.readByte());
			if(c=='\n'){
				return buffer.toString();
			}
			buffer.append(c);			
		}
		in.resetReaderIndex();
		return null;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, String msg, ByteBuf out)
			throws Exception {
		if(!msg.endsWith("\n")){
			msg +="\n";
		}
		int size = msg.length();
		byte [] buffer = new byte[size];
		for(int i =0; i < size; i++){
			buffer[i]=encode(msg.charAt(i));
		}
	}

}
