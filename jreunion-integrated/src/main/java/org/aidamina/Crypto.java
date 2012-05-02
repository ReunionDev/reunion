package org.aidamina;

import org.springframework.integration.Message;
import org.springframework.stereotype.Service;


@Service
public class Crypto {
	
	public String decrypt(Message<byte[]> message){
		
		byte [] payload =  message.getPayload();
		
		return new String(payload);		
	}
	
	public byte[] encrypt(Message<String> message){
		
		String payload = message.getPayload();
		
		return payload.getBytes();
		
	}

}
