package netty;

import java.util.regex.Pattern;

import netty.packets.LoginPacket;

public class LoginParser implements Parser{
	private Integer version;				

	private final static Pattern loginRegex = Pattern.compile("^login|play$");
	
	private String login;
	
	private String username;
	
	private String password;
	
	private boolean validate(){
		if(version==null){
			return false;
		}
		if(username==null){
			return false;
		}
		if(password==null){
			return false;
		}
		if(login==null){
			return false;
		}
		if(!loginRegex.matcher(login).matches()){
			return false;
		}
		
		return true;
	}
	
	private Packet build(){
		LoginPacket packet = new LoginPacket();
		packet.setVersion(version);
		packet.setReconnect(login.equals("play"));
		packet.setUsername(username);
		packet.setPassword(password);
		return packet;
		
	}
	
	@Override
	public Packet parse(String input) {		
		Packet packet = null;
		if(version==null){
			version = Integer.parseInt(input);
		}else if(login==null){
			login = input;
		}else if(username==null){
			username = input;
		}else if(password==null) {
			password = input;
			if(validate()){				
				packet = build();			
			}
		}
		return packet;
	}			

}
