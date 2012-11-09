package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class SayParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^say (-?\\d+) (.*)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		SayPacket packet = new SayPacket();
		int n = 0;
		
		packet.setId(Integer.parseInt(match.group(++n)));
		
		packet.setMessage(match.group(++n));
		
		return packet;
	}

}
