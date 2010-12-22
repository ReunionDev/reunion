package com.googlecode.reunion.jreunion.protocol;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;

public class OtherProtocol extends Protocol {

	public static Pattern locationRegex = Pattern.compile("(place|walk) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)(?: (?:-?\\d+) (?:-?\\d+))?\\n");

	short [] location = new short [4];
	public short iter = -1;
	public short iterCheck = -1;
	
	private BufferedOutputStream bos;
	
	public OtherProtocol(Client client) {
		super(client);
		
		try {
			bos = new BufferedOutputStream(new FileOutputStream("OtherProtocol-"+(new Date().getTime()/1000)+".txt", true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(client!=null){
			setAddress(getClient().getSocketChannel().socket().getLocalAddress());
			setPort((short)getClient().getSocketChannel().socket().getLocalPort());
			setVersion(getClient().getVersion());
			for(Map map :Server.getInstance().getWorld().getMaps()){
				if(map.getAddress().getAddress().equals(address)&&map.getAddress().getPort()==port) {
					mapId = map.getId();
				}
			}
		}
	}
	
	short magic0 = -1;
	short magic1 = -1;
	boolean isLastLocationPlace = false;
	
	private InetAddress address;
	private int port = 4005;
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
		this.magic0 = magic(address,0);
		this.magic1 = magic(address,1);
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	private int version = -1;
	private int mapId = 4;

	@Override
	public String decryptServer(byte[] data) {
			
		String result = decryptServer(data, iter, iterCheck);		
		handleChanges(result);
		return result;
	}
	
	public void handleChanges(String data) {
		
		if(data.contains("walk")||data.contains("place")) {
			Matcher matcher = locationRegex.matcher(data);
			while(matcher.find()) {
				
				isLastLocationPlace = matcher.group(1).equals("place");
				
				for(int i=0; i<location.length; i++){
					location[i] = Short.parseShort(matcher.group(i + 2));
				}
			}
		}
		
		if(data.contains("encrypt_key")){
			String debug = "data:\n";
			debug+=data;
			debug+="isLastLocationPlace: "+isLastLocationPlace+"\n";
			debug+="place: "+location[0] +" "+ location[1] +" "+ location[2]+"\n";
			debug+="before: \n";
			debug+="iter: "+iter+"\n";;
			debug+="iterCheck: "+iterCheck+"\n";
			
			
			System.out.println("place: "+location[0] +" "+ location[1] +" "+ location[2]);
			short magicx = -1;
			if(isLastLocationPlace) {
				magicx =  (short)(magic1 + location[0] + location[1] - location[2]);
			} else {
				magicx = (short)Math.abs((short)(location[0] + location[1] - magic0));
			}
			
			debug+="old magicx: "+magicx+"\n";
			magicx = (short) func_unknown_encrypt_related(location[0], location[1], location[2], location[3],isLastLocationPlace);
			
			
			debug+="new magicx: "+magicx+"\n";
			
			iter =  (short)(magicx % 4);
			iterCheck += (magic1 ^ (magicx + 2 * magic1 - mapId));
			
			debug+="after: \n";
			debug+="iter: "+iter+"\n";;
			debug+="iterCheck: "+iterCheck+"\n";
			
			try {
				bos.write(debug.getBytes());
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(debug);
		}
	}
	
	public String decryptServer(byte[] data, short iter, short iterCheck) {
		String result = null;
		switch(iter + 1){
			case 0:
				int magic3 = (port - 17) % 131;
				for(int i = 0 ;i < data.length; i++){
					data[i]=(byte)(((magic0 ^ data[i]) + 49) ^ magic3);
				}
				result = new String(data);
				break;
			case 1:
				short magic4 = (short)(iterCheck - version + 10);
				for(int i = 0;i < data.length; i++){
					data[i]=(byte)(data[i]^magic4^version);
				}
				result = new String(data);
				break;
			case 2:
				short magic5 = (short)(iterCheck + magic1 - magic0);
				for(int i = 0;i < data.length; i++){
					data[i]=(byte)(((data[i]^magic5)-19) ^ mapId);
				}
				result = new String(data);
				break;
			case 3:
				short magic6 = (short)(port + 3 * mapId + mapId % 3);
				for(int i = 0; i < data.length; i++){
					data[i]=(byte)(((data[i]^magic6)^iterCheck)+4);
				}
				result = new String(data);
				break;
			case 4: 
				for(int i = 0; i < data.length; i++){
					data[i]=(byte)(((data[i]^(iterCheck+111))+33)^version);
				}
				result = new String(data);
				break;
			default:
				throw new RuntimeException("Unable to Decrypt");
		}		
		return result;
	}
	
	@Override
	public String decryptClient(byte[] data){
		
		int magic4 = magic0 - port - mapId + version;
		for(int i=0;i<data.length;i++){
			
			int step1 = magic1 ^ data[i];
			int step2 = step1 - 19;			
			int step3 = step2 ^ magic4;			
			data[i] = (byte)step3;
		}		
		return new String(data);
	}
	
	@Override
	public byte[] encryptClient(String packet){
		
		byte[] result = encryptClient(packet, iter, iterCheck);
		handleChanges(packet);
		
		return result;
	}
	
	public byte [] encryptClient(String packet, short iter, short iterCheck) {
		byte [] data = packet.getBytes();
		switch(iter + 1){
			case 0:
				int magic3 = (port - 17) % 131;
				for(int i = 0; i < data.length; i++) {
					data[i] = (byte)(magic0 ^ ((data[i] ^ magic3) - 49));
				}
				break;
			case 1:
				int magic4 = iterCheck - version + 10;
				for(int i = 0; i < data.length; i++) {
					data[i] = (byte)(data[i] ^ version ^ magic4);
				}
				break;
			case 2:
				int magic5 = iterCheck + magic1 - magic0;
				for(int i = 0; i < data.length; i++) {
					data[i]=(byte)(magic5^((mapId ^ data[i]) + 19));
				}
				break;
			case 3: // ?
				int magic6 = port + 3 * mapId + mapId % 3;
				for(int i = 0; i < data.length; i++) {
					data[i] = (byte)(((iterCheck ^ data[i]) - 4) ^ magic6);
				}
				break;
			case 4: 
				for(int i = 0; i < data.length; i++) {
					data[i] = (byte)((iterCheck + 111) ^ ((data[i] ^ version) - 33));
				}
				break;
			default:
				throw new RuntimeException("Unable to Encrypt");
		}
		return data;
	}
	
	@Override
	public byte[] encryptServer(String packet) {
	
		//refresh version because its not always available on connect
		if(getVersion() == -1 && getClient()!=null)
			setVersion(getClient().getVersion());
		if(mapId == -1) {
			throw new RuntimeException("Invalid Map");
		}
		
		int magic4 = magic0 - port - mapId + version;
		byte [] data = packet.getBytes();
		for(int i = 0; i<data.length; i++) {
			int rstep3 = data[i] ^ magic4;
			int rstep2 = rstep3 + 19;
			int rstep1 = magic1 ^ rstep2;
			data[i] = (byte)rstep1;
		}
		return data;
	}
	
	public static short magic(InetAddress ip, int mask)
	{
		byte [] rip = ip.getAddress();
		
		if ( mask == 1 )
			return (short)(rip[0] ^ rip[1] ^ rip[2] ^ rip[3]);
		else
			return (short)(rip[0] + rip[1] + rip[2] + rip[3]);	   
	}
	
	public static void main(String[] args)  {

		
		String data = "walk 6926 5091 106 1\n"
						+ "encrypt_add 11116034\n"
						+ "encrypt_multi 11123587\n"
						+ "encrypt_password 11140473\n"
						+ "encrypt_key 11124185\n";
		
		OtherProtocol op = new OtherProtocol(null);
		
		try {
			op.setAddress(InetAddress.getByName("93.90.190.134"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		op.setVersion(2000);
		op.setPort((short)4005);
		op.setMapId(4);
		
		op.handleChanges(data);
	}
	
	int func_unknown_encrypt_related(int x, int y, int z, int rotation, boolean a6)
	{
		int magic;
		int version;
		int v12;
		int v13;
		int v14;
		int v15;
		int v16;
		int result;
		boolean v19;
		int v20;

		magic = rotation + x + y + z;
		
		if ( a6 )
		{
			version = 100;
		}
		else
		{
			version = 2000;//version

		}
		v13 = version - mapId + magic;
		v12 = v13;
		if ( v13 < 0 )
			v12 = -v13;

		if ( a6 )
		{
			v14 = v12 % 3;
			if (v14==0 )
			{
				v15 = z;
				if ( v13 < 0 )
				{
					//continue LABEL_10;
					{
						v16 = v15 + 1;						
					}
				}else{
					LABEL_21:
						v16 = v15 - 1;
				}
				//continue LABEL_11;
				{
					z = v16;
					//continue LABEL_12;
					{
						if ( a6 )
						{
							result = magic1 + x + y - z;
						}
						else
						{
							result = Math.abs(x + y - magic0);
						}
						return result;
					}					
				}
			}
			v19 = v14 == 1;
		}
		else
		{
			v20 = v12 % 3;
			if ( v20 == 2 )
			{
				v15 = z;
				if ( v13 >= 0 )
				{
					v16 = v15 - 1;
				}
				else{
					LABEL_10:
						v16 = v15 + 1;
				}
				LABEL_11:
					z = v16;
				//continue LABEL_12;
				{
					if ( a6 )
					{
						result = magic1 + x + y - z;
					}
					else
					{
						result = Math.abs(x + y - magic0);
					}
					return result;
				}
			}
			v19 = v20 == 0;
		}
		if ( !v19 )
		{
			LABEL_12:
				if ( a6 )
				{
					result = magic1 + x + y - z;
				}
				else
				{
					result = Math.abs(x + y - magic0);
				}
			return result;
		}
		v15 = z;
		if ( v13 >= 0 ){
			//continue LABEL_10;
			{
				v16 = v15 + 1;
				z = v16;
				if ( a6 )
				{
					result = magic1 + x + y - z;
				}
				else
				{
					result = Math.abs(x + y - magic0);
				}
				return result;
				
			}
		}
		//continue LABEL_21;
		{
			v16 = v15 - 1;	
			z = v16;
			
			if ( a6 )
			{
				result = magic1 + x + y - z;
			} else {
				result = Math.abs(x + y - magic0);
			}
			return result;
					
		}

	}

	
}
