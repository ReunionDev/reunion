package org.reunionemu.jreunion.protocol;

import java.lang.annotation.*;
import java.util.regex.*;


public interface PacketParser {
	public Pattern getPattern();

	public Packet parse(Matcher matcher, String input);

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Server {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Client {
	}

}
