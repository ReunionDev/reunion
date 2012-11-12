package org.reunionemu.jreunion.protocol.parsers.server.test;

import static org.junit.Assert.*;

import java.util.regex.*;


import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.SkillUpPacket;
import org.reunionemu.jreunion.protocol.parsers.server.SkillUpParser;

public class SkillUpParserTest {

	@Test
	public void test() {
		SkillUpParser parser = new SkillUpParser();
		Pattern pattern = parser.getPattern();

		int skillId = 1;
		String msg = "skillup " + skillId;
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("skillup" + skillId).matches());
		assertFalse(pattern.matcher("skillup").matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof SkillUpPacket);
		assertEquals(skillId, ((SkillUpPacket) packet).getId());
		assertEquals(msg, packet.toString());

	}

}
