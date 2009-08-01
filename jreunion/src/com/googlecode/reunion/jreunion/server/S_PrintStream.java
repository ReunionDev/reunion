package com.googlecode.reunion.jreunion.server;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PrintStream extends PrintStream {
	BufferedWriter filebuffer;
	String logfilename;
	boolean newline ;

	private S_PrintStream(OutputStream out) {
		super(out, true);			
		openLogfile();
	}
	
	/* (non-Javadoc)
	 * @see java.io.PrintStream#format(java.lang.String, java.lang.Object...)
	 */
	@Override
	public PrintStream format(String arg0, Object... arg1) {
		
		return super.format(arg0, arg1);
	}

	private void openLogfile()
	{
		newline = true;
		try {
			logfilename = generateLogfileName();
			new File("logs").mkdir();
			if (filebuffer!=null)
				filebuffer.close();
			filebuffer = new BufferedWriter(new FileWriter(logfilename, true));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see java.io.PrintStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] arg0, int arg1, int arg2) {
	
		super.write(arg0, arg1, arg2);
		checkLogfile();
		
		try {
			
			for (int i = 0 ;i<arg2;i++)
			{
				
				if (newline==true)	
				{
					String timestring = DateFormat.getTimeInstance().format(new Date());
					filebuffer.write(timestring+" | ");
					
				}
				filebuffer.write(arg0[arg1+i]);
				
				if (arg0[arg1+i]==10) 
					newline=true;
				else
					newline=false;
			}
			
			
			filebuffer.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}

	public static void useFileLogging()
	{
		S_PrintStream ps = new S_PrintStream(System.out  );
		System.setOut(ps);
		System.setErr(ps);
	}
	private String generateLogfileName()
	{
		String path="logs/";
		Calendar c = Calendar.getInstance();
		
		//Date df = new Date();
		//String filename = df.getDate() + "-" + (df.getMonth() + 1) + "-"
		//+ (df.getYear() + 1900) + ".txt";
		String filename = c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
		+ c.get(Calendar.YEAR) + ".txt";
		return path+filename;
	}
	private void checkLogfile()
	{
		if (!logfilename.equalsIgnoreCase(generateLogfileName()))
		{		 
			openLogfile();
		}
	}
	
}
