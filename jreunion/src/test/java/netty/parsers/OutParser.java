package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.packets.OutPacket.EntityType;
import netty.packets.UseSkillPacket.TargetType;
import netty.parsers.PacketParser.Server;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Server
public class OutParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^out (n|c|item|p) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		OutPacket packet = new OutPacket();
		int n = 0;
		String type = match.group(++n);
		if (type != null) {
			if (type.equals("n")) {
				packet.setEntityType(EntityType.NPC);
			} else if (type.equals("c")) {
				packet.setEntityType(EntityType.CHAR);
			} else if (type.equals("item")) {
				packet.setEntityType(EntityType.ITEM);
			} else if (type.equals("p")) {
				packet.setEntityType(EntityType.PET);
			}
		}
		packet.setId(Long.parseLong(match.group(++n)));

		return packet;
	}

}
