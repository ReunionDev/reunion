package com.googlecode.reunion.jreunion.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Area {
	    
	public static enum Field {
		PVP,
		MOB,
		PLAYER;
	}
	public int mapSizeX = 1280, mapSizeY = 1280;
	private byte data[];
	
	public Area() {
		int bits = Field.values().length * mapSizeX * mapSizeY;
		int size = (bits/8)+(bits%8>0?1:0);
		data = new byte[size];
	}
	
	public void set(int x, int y, Field field, boolean value) {
		
		int nbit = getPos(x,y,field);
		int nbyte = nbit/8;
		int noffset = nbit%8;		
		int val = 1 << noffset;		
		if(value){
			data[nbyte] |=val;
		}else{
			data[nbyte] &=~val;
		}
	}
	private int getPos(int x, int y, Field field) {
		
		return ((x +(y * mapSizeX))*Field.values().length)+field.ordinal();
	}
	
	public boolean get(int x, int y, Field field){
		int nbit = getPos(x, y, field);
		int nbyte = nbit/8;
		int noffset = nbit%8;
		return (data[nbyte] & (1 << noffset)) != 0;
	}

	public boolean load(String filename, Field field) {
		File file = new File(filename);
		if(!file.exists()){
			Logger.getLogger(this.getClass()).error("File not found: "+file);
			return false;			
		}

		try {
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			 if (readers.hasNext()) {

                ImageReader reader = readers.next();
                reader.setInput(iis, true);                
                BufferedImage image = reader.read(0);
                
                if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) {
                	throw new Exception("Invalid image type: "+image.getType()+" expecting BufferedImage.TYPE_BYTE_BINARY("+BufferedImage.TYPE_BYTE_BINARY+")");
                }

                if(image.getHeight() != mapSizeY || image.getWidth() != mapSizeX) {
                	throw new Exception("Invalid image size: "+image.getWidth()+"/"+image.getHeight()+" expected: "+mapSizeX+"/"+mapSizeY);
                }
                
                for(int y = 0;y<image.getHeight();y++) {
	                for(int x = 0;x<image.getWidth();x++)
	                {
	                	this.set(x, y, field, (byte)image.getRGB(x, y)!=0);
	                }
                }
                
            } else {
            	throw new Exception("No valid reader found for file: " + file);            	
            }
			//Logger.getLogger(Area.class).info("done loading: " + file);

			return true;

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error("Exception",e);
			return false;
		}
	}
}
