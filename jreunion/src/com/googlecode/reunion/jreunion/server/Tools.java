package com.googlecode.reunion.jreunion.server;

public class Tools {
	public static int statCalc(int n,int count){
		int coef = (n/count);	
		return(int) ((0.5 * coef * (1 + coef)) * count) + (coef + 1) * (n % count);
	}
}
