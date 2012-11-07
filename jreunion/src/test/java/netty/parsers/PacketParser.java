package netty.parsers;

import java.lang.annotation.*;
import java.util.regex.*;

import netty.Packet;

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
