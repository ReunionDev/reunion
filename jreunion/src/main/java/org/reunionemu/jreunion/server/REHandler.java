package org.reunionemu.jreunion.server;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class REHandler implements Runnable {
    
	Runnable delegate;
    
    public REHandler (Runnable delegate) {
        this.delegate = delegate;
    }
    public void run () {
        try {
            delegate.run ();
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass()).error(e);
        }
    }
}