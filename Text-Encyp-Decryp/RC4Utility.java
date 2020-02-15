package com.sample.projectsample;

import java.util.Scanner;

/**
 * Utility class for RC4 (PRGA and IPRGA) implementation
 * 
 * @author Sanjith
 *
 */
public class RC4Utility {

	/**
	 * For swapping the entries in the position i and j the the array
	 * 
	 * @param s
	 * @param i
	 * @param j
	 */
	public static void swap(int[] s, int i, int j) {
		if (i == j) {
			return;
		} else {
			s[i] = s[i] + s[j];
			s[j] = s[i] - s[j];
			s[i] = s[i] - s[j];
		}
	}

/**
 * For printing the integer array
 * @param array
 */
	public static void printArray(int[] array) {

		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + "  ");
		}
		System.out.println();
	}

}
