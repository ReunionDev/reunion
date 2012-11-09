package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.SkillUpPacket;
import netty.parsers.SkillUpParser;

import org.junit.Test;

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
