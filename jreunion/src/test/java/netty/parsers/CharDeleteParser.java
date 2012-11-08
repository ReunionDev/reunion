package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.CharDeletePacket;
import netty.parsers.PacketParser.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class CharDeleteParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^char_del (\\d+)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new CharDeletePacket(Integer.parseInt(match.group(1)));
	}

}
