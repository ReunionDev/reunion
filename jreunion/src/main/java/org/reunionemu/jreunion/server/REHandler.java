package org.reunionemu.jreunion.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage(),e);
        }
    }
}