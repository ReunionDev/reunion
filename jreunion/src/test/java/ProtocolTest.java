import static org.junit.Assert.*;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.Protocol;


public class ProtocolTest {
	
	private String [] passing = {
		"1111\nlogin\ntest\n",
		"1111\nlogin\ntest\n\n",
		"1111\r\n",
		"1111\r",
		"1111\n",
		"1111\nlogin\ntest\ntest\n",
		"1111\nlogin\n",
		"1111\nplay\n",
		"1111\nlogin\ntest\ntest\n..\n..\n..\nstart_game 0 1\n",
		};
	private String [] failing = {
			"1111\nlogin\ntest",
			"1111",
			"",
			"1111\nlogin",
			"1111\nplay",
			"1111\nlogin\ntest\ntest\n..",
			"\r\n",
			"\n",			
		};
	
	/*
	 * Test our protocol detection function
	 * 
	 */
	@Test
	public void testTestLogin() { 
		for(String test : passing){
			assertTrue("expected true from testLogin(\""+ test+"\")", Protocol.testLogin(test));
			
		}
		for(String test : failing){
			assertFalse("expected false from testLogin(\""+ test+"\")", Protocol.testLogin(test));
			
		}
	}

}
