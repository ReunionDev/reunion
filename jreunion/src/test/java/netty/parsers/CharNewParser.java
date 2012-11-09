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
public class CharNewParser implements PacketParser {

	static final Pattern regex = Pattern
			.compile("^char_new (\\d+) ([^ ]+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)$");

	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		CharNewPacket packet = new CharNewPacket();
		int n = 0;
		packet.setSlot(Integer.parseInt(match.group(++n)));

		packet.setName(match.group(++n));

		packet.setRace(Race.byValue(Integer.parseInt(match.group(++n))));

		packet.setSex(Sex.byValue(Integer.parseInt(match.group(++n))));

		packet.setHair(Integer.parseInt(match.group(++n)));

		packet.setStrength(Integer.parseInt(match.group(++n)));

		packet.setIntellect(Integer.parseInt(match.group(++n)));

		packet.setDexterity(Integer.parseInt(match.group(++n)));

		packet.setConstitution(Integer.parseInt(match.group(++n)));

		packet.setLeadership(Integer.parseInt(match.group(++n)));

		return packet;
	}

}
