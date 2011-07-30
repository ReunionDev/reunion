package com.googlecode.reunion.jreunion.server;

public class Tools {

	/**
	 * Calculate how much a status increases an attribute
	 * @param n input status
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
	
	/**
	 * Calculate a random success rate from a given value
	 * @param limit
	 * @return true if given limit is greater then random success rate
	 */
	public static boolean successRateCalc(float limit){
		return limit > Server.getRand().nextDouble();
	}
}
