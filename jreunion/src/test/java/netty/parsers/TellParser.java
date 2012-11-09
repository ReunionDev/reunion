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
public class TellParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^tell ([^ ]+) (.+)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		TellPacket packet = new TellPacket();
		int n = 0;
		
		packet.setName(match.group(++n));
		
		packet.setMessage(match.group(++n));
		
		return packet;
	}

}
