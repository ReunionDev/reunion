package org.reunionemu.jreunion.protocol;


public interface Protocol {
	
	public byte encode(char c);
	
	public char decode(byte b);

}
