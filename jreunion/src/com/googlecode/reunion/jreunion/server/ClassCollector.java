package com.googlecode.reunion.jreunion.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassCollector {
	
	private static java.util.Map<Package, List<Class<?>>> classes = new HashMap<Package, List<Class<?>>>();
	
	public synchronized static List<Class<?>> getClasses(){
		List<Class<?>> pkgClasses = new LinkedList<Class<?>>();
		Package[] pkgs = Package.getPackages();
		for (Package pkg: pkgs) {
			List<Class<?>> cls = getClasses(pkg);
			if(cls!=null){
				pkgClasses.addAll(cls);
			}
		}
		return pkgClasses;
	}
	
	public synchronized static List<Class<?>> getClasses(Package pkg){		
		List<Class<?>> cls = classes.get(pkg);
		if(cls==null){
			try{
				cls = getClassesForPackage(pkg);
				classes.put(pkg, cls);
			}catch(Exception e){ }
		}
		if(cls!=null){
			return cls;
		}else{
			return new LinkedList<Class<?>>();
		}
	}
	
	private static List<Class<?>> getClassesForPackage(Package pkg) {
	    String pkgname = pkg.getName();
	    List<Class<?>> classes = new LinkedList<Class<?>>();
	    // Get a File object for the package
	    File directory = null;
	    String fullPath;
	    String relPath = pkgname.replace('.', '/');
	    //System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
	    //System.out.println("ClassDiscovery: Resource = " + resource);
	    if (resource == null) {
	        throw new RuntimeException("No resource for " + relPath);
	    }
	    fullPath = resource.getFile();
	    //System.out.println("ClassDiscovery: FullPath = " + resource);
	    try {
	        directory = new File(resource.toURI());
	    } catch (URISyntaxException e) {
	        throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
	    }
	    //System.out.println("ClassDiscovery: Directory = " + directory);
	    if (directory.exists()) {
	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {
	            // we are only interested in .class files
	            if (files[i].endsWith(".class")) {
	                // removes the .class extension
	                String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
	                //System.out.println("ClassDiscovery: className = " + className);
	                try {
	                    classes.add(Class.forName(className));
	                } 
	                catch (ClassNotFoundException e) {
	                    throw new RuntimeException("ClassNotFoundException loading " + className);
	                }
	            }
	        }
	    } else {
	        try {
	            String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
	            JarFile jarFile = new JarFile(jarPath);         
	            Enumeration<JarEntry> entries = jarFile.entries();
	            while(entries.hasMoreElements()) {
	                JarEntry entry = entries.nextElement();
	                String entryName = entry.getName();
	                if(entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
	                    //System.out.println("ClassDiscovery: JarEntry: " + entryName);
	                    String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
	                    //System.out.println("ClassDiscovery: className = " + className);
	                    try {
	                        classes.add(Class.forName(className));
	                    } 
	                    catch (ClassNotFoundException e) {
	                        throw new RuntimeException("ClassNotFoundException loading " + className);
	                    }
	                }
	            }
	        } catch (IOException e) {
	            throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
	        }
	    }
	    return classes;
	}
}
