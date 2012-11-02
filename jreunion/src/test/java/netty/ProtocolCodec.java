package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class ProtocolCodec extends ByteToMessageCodec<String, String> {

	ProtocolFactory factory;

	public ProtocolCodec(ProtocolFactory factory) {
		if (factory == null) {
			throw new NullPointerException();
		}
		this.factory = factory;
	}

	@Override
	public String decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		Protocol protocol = factory.getProtocol(ctx.channel());
		if (protocol == null) {
			throw new NullPointerException();
		}
		in.markReaderIndex();
		StringBuffer buffer = new StringBuffer();
		int size = in.readableBytes();
		for (int i = 0; i < size; i++) {
			char c = protocol.decode(in.readByte());
			if (c == '\n') {
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
		Protocol protocol = factory.getProtocol(ctx.channel());
		if (protocol == null) {
			throw new NullPointerException();
		}
		if (!msg.endsWith("\n")) {
			msg += "\n";
		}
		int size = msg.length();
		byte[] buffer = new byte[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = protocol.encode(msg.charAt(i));
		}
		out.writeBytes(buffer);
	}
}
