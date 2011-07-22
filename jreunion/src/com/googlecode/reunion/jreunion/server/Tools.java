package com.googlecode.reunion.jreunion.server;

public class Tools {

	/**
	 * Caculate how much a stat increases an attribute
	 * @param n input stat
	 * @param count increment boundary
	 * @return increased value
	 */
	public static int statCalc(int n, int count) {
		int coef = (n / count);
		return (int) ((0.5 * coef * (1 + coef)) * count) + (coef + 1)
				* (n % count);
	}

	
	/**
	 * Normalize a value between upper and lower bounds 
	 * TODO: Use Generics
	 * @param current
	 * @param min
	 * @param max
	 * @return
	 */
	public static int between(int current, int min, int max) {

		return Math.max(Math.min(current, max), min);

	}
}
