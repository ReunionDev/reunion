package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.CombatPacket;
import netty.parsers.PacketParser.Client;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
@Client
public class CombatParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^combat(?: (\\d+))? (1|0)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CombatPacket packet = new CombatPacket();
		int n = 0;
		String id = match.group(++n);
		if(id!=null){
			packet.setId(Long.parseLong(id));			
		}		
		packet.setInCombat(match.group(++n).equals("1"));

		return packet;
	}
	

}
