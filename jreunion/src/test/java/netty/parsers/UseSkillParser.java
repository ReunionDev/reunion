package netty.parsers;

import java.util.regex.*;

import netty.Packet;
import netty.packets.*;
import netty.packets.UseSkillPacket.TargetType;
import netty.parsers.PacketParser.Client;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Client
public class UseSkillParser implements PacketParser {

	static final Pattern regex = Pattern.compile("^use_skill (\\d+) (?:(npc|char) (\\d+))?(?: ([^ ]+))?(?: ([^ ]+))?(?: ([^ ]+))?(?: ([^ ]+))?(?: ([^ ]+))?(?: ([^ ]+))?$"); 
	
	@Override
	public Pattern getPattern() {
		return regex;
	}

	@Override
	public Packet parse(Matcher match, String input) {
		UseSkillPacket packet = new UseSkillPacket();
		packet.setSkillId(Integer.parseInt(match.group(1)));
		
		String targetType = match.group(2);
		if(targetType!=null){
			if(targetType.equals("npc")){
				packet.setTargetType(TargetType.NPC);
			}else if(targetType.equals("char")){
				packet.setTargetType(TargetType.CHAR);
			}
		}
		String targetId = match.group(3);
		if(targetId!=null){
			packet.setTargetId(Integer.parseInt(targetId));			
			
		}
		for(int n=4; n<=match.groupCount();n++){
			String arg = match.group(n);
			if(arg!=null){
				packet.getArguments().add(arg);
			}
		}
		
		
		return packet;
	} 
	
	public static void main(String []args){
		Matcher matcher = regex.matcher("use_skill 113 npc 1100 a b c");
		
		
		
		if(matcher.matches()){	 	
			for(int groupN =0; groupN<=matcher.groupCount(); groupN++){
				System.out.println(matcher.group(groupN));
				
			}
		}
		
		
	}
	
	

}
