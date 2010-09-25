package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PrintStream extends java.io.PrintStream {
	public static void useFileLogging() {
		java.io.PrintStream ps = new java.io.PrintStream(System.out);
		System.setOut(ps);
		System.setErr(ps);
	}

	BufferedWriter filebuffer;
	String logfilename;

	boolean newline;

	private PrintStream(OutputStream out) {
		super(out, true);
		openLogfile();
	}

	private void checkLogfile() {
		if (!logfilename.equalsIgnoreCase(generateLogfileName())) {
			openLogfile();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.PrintStream#format(java.lang.String, java.lang.Object...)
	 */
	@Override
	public java.io.PrintStream format(String arg0, Object... arg1) {
		
		return super.format(arg0, arg1);
	}

	private String generateLogfileName() {
		String path = "logs/output/";
		Calendar c = Calendar.getInstance();

		// Date df = new Date();
		// String filename = df.getDate() + "-" + (df.getMonth() + 1) + "-"
		// + (df.getYear() + 1900) + ".txt";
		String filename = c.get(Calendar.DAY_OF_MONTH) + "-"
				+ (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR)
				+ ".txt";
		return path + filename;
	}

	private void openLogfile() {
		newline = true;
		try {
			logfilename = generateLogfileName();
			new File("logs").mkdir();
			new File("logs/output").mkdir();
			if (filebuffer != null) {
				filebuffer.close();
			}
			filebuffer = new BufferedWriter(new FileWriter(logfilename, true));
		} catch (IOException e) {

			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.PrintStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] buff, int off, int len) {

		super.write(buff, off, len);
		checkLogfile();

		try {

			for (int i = 0; i < len; i++) {

				if (newline == true) {
					String timestring = DateFormat.getTimeInstance().format(
							new Date());
					filebuffer.write(timestring + " | ");

				}
				filebuffer.write(buff[off + i]);

				if (buff[off + i] == 10) {
					newline = true;
				} else {
					newline = false;
				}
			}

			filebuffer.flush();
		} catch (IOException e) {

			Logger.getLogger(this.getClass()).warn("Exception",e);
		}

	}

}
