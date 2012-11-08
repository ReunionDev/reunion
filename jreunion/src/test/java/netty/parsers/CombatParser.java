package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.packets.UseSkillPacket.TargetType;
import netty.parsers.PacketParser.Client;

import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class CombatParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^combat (\\d+) (1|0)$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CombatPacket packet = new CombatPacket();
		int n = 0;
		packet.setId(Integer.parseInt(match.group(++n)));		
		
		packet.setInCombat(match.group(++n).equals("1"));

		return packet;
	}
	

}
