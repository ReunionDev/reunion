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
	 * @param current
	 * @param min
	 * @param max
	 * @return
	 */
	public static <T extends Comparable<T>> T between(T current, T min, T max) {
		T value = current;
		if(value.compareTo(max)>0){
			value = max;
		}else if(value.compareTo(min)<0){
			value = min;
		}
		return value;
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
