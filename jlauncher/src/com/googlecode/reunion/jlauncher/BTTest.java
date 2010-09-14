package com.googlecode.reunion.jlauncher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

import org.yaircc.torrent.bencoding.BDecodingException;
import org.yaircc.torrent.bencoding.BEncodedInputStream;
import org.yaircc.torrent.bencoding.BMap;
import org.yaircc.torrent.bencoding.BTypeException;
import org.yaircc.torrent.data.PeerInfo;
import org.yaircc.torrent.protocol.BTMessageInputStream;
import org.yaircc.torrent.tracker.TrackerResponse;

public class BTTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws BDecodingException 
	 */
	public static void main(String[] args) throws IOException, BDecodingException {
		String version = "";
		//[truncated] GET /announce?info_hash=C%0d5%1f%8f%8b%b4%07%bc%80%c7%20%ddX0%be%3c%82%d4%e1&peer_id=-UT2040-RT%9e%94ZR%82%ea%e2%91%29%af&port=45293&uploaded=0&downloaded=0&left=0&corrupt=0&key=7B5BF69E&event=stopped&numwant=0&compact=1&no_pee
		Random r = new Random(System.currentTimeMillis());
		String test ="ABCDEFGHIJKLMNOPQRST";
		String client = "JLAUNCHER-";
		byte [] rnd = new byte[20-client.length()];
		r.nextBytes(rnd);
		
		client+= new String(rnd);
		
		client = URLEncoder.encode(client,"US-ASCII");
		String hash = "C%0d5%1f%8f%8b%b4%07%bc%80%c7%20%ddX0%be%3c%82%d4%e1";
		System.out.println(new BigInteger(URLDecoder.decode(hash).getBytes()).toString(16));
		URL url = new URL("http://tracker.openbittorrent.com:80/announce?info_hash="+hash+"&peer_id="+client+"&port=6882&compact=1");
		
		try {
			
			System.out.println(url);
			InputStream is = url.openStream();
			BEncodedInputStream bis =new BEncodedInputStream(is);
			BMap o = (BMap) bis.readElement();
			TrackerResponse tr = new TrackerResponse(o);
			List<PeerInfo> peers = tr.getPeerList();
			System.out.println(peers.size());	
			for(PeerInfo info : peers){
				System.out.println(info.getAddress());				
				
			}
			System.out.println(o);
			
			
			Socket s = new Socket("127.0.0.1",45293);
			BTMessageInputStream cl = new BTMessageInputStream();
					
			
			is = s.getInputStream();
			DataInputStream in = new DataInputStream(is);
			
			System.out.println("listening");
	        while( true) {
	        	if(is.available()>0){
	        	byte [] buffer = new byte [is.available()];
	            System.out.println("received"+buffer.length);
	        	in.readFully(buffer);
	        	cl.update(buffer);
	        	}
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  
		}

	}

}
