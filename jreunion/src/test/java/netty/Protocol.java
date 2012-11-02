package netty;


public interface Protocol {
	
	public byte encode(char c);
	
	public char decode(byte b);

}
