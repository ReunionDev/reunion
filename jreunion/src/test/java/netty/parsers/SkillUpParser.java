package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.SkillUpPacket;
import netty.parsers.PacketParser.Client;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class SkillUpParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^skillup (\\d+)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		return new SkillUpPacket(Integer.parseInt(match.group(1)));
	}

}
