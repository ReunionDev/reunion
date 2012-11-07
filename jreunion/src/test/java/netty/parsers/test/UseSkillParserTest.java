package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.UseSkillPacket;
import netty.parsers.UseSkillParser;

import org.junit.Test;

public class UseSkillParserTest {

	@Test
	public void test() {
		UseSkillParser parser = new UseSkillParser();
		Pattern pattern = parser.getPattern();
		
		String msg = "use_skill 113 npc 1100 a b c";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());	
		
		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof UseSkillPacket);	
		assertEquals(msg, packet.toString());		
		
	}
	


}
