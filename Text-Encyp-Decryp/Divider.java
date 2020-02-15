package com.sample.projectsample;

import java.io.UnsupportedEncodingException;


/**
 * @author Sanjith
 * Class used to perform the Division operation while creating the hash function.
 */
public class Divider {
	
	/**
	 * Method responsible for dividing the message in to messages for size 512bits.
	 * @param message
	 * 			The message to be divided
	 * @return
	 * 			Returns the messages of 512bits in array of String.
	 * @throws UnsupportedEncodingException
	 */
	public static String[] divide(String message) throws UnsupportedEncodingException{
		int length =	message.getBytes("UTF-8").length;
		int numberOfDivision = length/64; //64 byte = 512 bits
		String[] stringArray = new String[numberOfDivision]; 
		
		int beginningIndex = 0;
		int endingIndex = 64;
		for (int i = 0; i < numberOfDivision; i++) {
			stringArray[i]= message.substring(beginningIndex, endingIndex);
			beginningIndex = endingIndex;
			endingIndex = endingIndex + 64;			
		}
		return stringArray;
	}
	
}
