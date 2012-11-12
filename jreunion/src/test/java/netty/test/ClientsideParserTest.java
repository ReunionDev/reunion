package netty.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import netty.*;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.PacketParser;
import org.reunionemu.jreunion.protocol.parsers.client.FailParser;
import org.reunionemu.jreunion.protocol.parsers.server.UseSkillParser;

public class ClientsideParserTest {

	@Test
	public void test() {
		ClientsideParser parser = new ClientsideParser();
		parser.setParsers(Arrays.asList(new PacketParser[]{new FailParser()}));		
		assertEquals(1, parser.size());
		parser.setParsers(Arrays.asList(new PacketParser[]{new FailParser(), new UseSkillParser()}));		
		assertEquals(1, parser.size());
	}

}
