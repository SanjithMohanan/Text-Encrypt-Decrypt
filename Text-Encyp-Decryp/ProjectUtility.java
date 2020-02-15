package com.sample.projectsample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Utility class for the project. Contains utility methods for implementing PRGA,IPRGA,RC4 encryption/decryption operations. 
 * 
 * @author Sanjith
 *
 */
public class ProjectUtility {
	private static Scanner numScanner;
	public static int padCount;

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
			// System.out.println("i = " + i + " j = " + j);
			s[i] = s[i] + s[j];
			s[j] = s[i] - s[j];
			s[i] = s[i] - s[j];
		}
	}


	/**
	 * Method responsible for reading the sequence in which the messages are to be send.
	 * @param packetCount
	 * @return
	 */
	public static int[] readSequenceOrder(int packetCount) {
		int size = packetCount;
		System.out.println(size + " packets will be send. Enter the order in which packets need to be send (0 to " + (size-1) + ")");
		System.out.println("Enter the Sequence order :: ");
		int[] key = new int[size];

		for (int i = 0; i < size; i++) { // Reading the values for the key one
											// at a time.
			numScanner = new Scanner(System.in);
			if (numScanner.hasNextInt()) { // Checking whether the each value of
				int temp = numScanner.nextInt();
				if(temp >= size){
					System.out.println("number should be between 0 and " + (packetCount -1) );
					i--;
				}else{
					key[i] = temp+1;
				}
				
			} else {
				System.out.println("Please enter valid values for the key");
				--i;
			}
		}

		System.out.println("Sequence order is :: ");
		ProjectUtility.printArray(key);
		return key;
	}

	/**
	 * This method will split a string in to strings of length 512bits. 
	 * @param msg
	 * 		message to be split
	 * @return
	 */
	public static String[] splitMessage(String msg){
		
		String content = msg;
		String[] msgArray = new String[msg.length()/252];
		int i=0;
		while(content.length() >= 252) {
		    msgArray[i] = content.substring(0, 252);
		    i++;
		    content = content.substring(252);
		}
		return msgArray;
	}
	
	/**
	 * For printing the array of content.
	 * @param array
	 * 			array to be printed.
	 */
	public static void printArray(int[] array) {

		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + "  ");
		}
		System.out.println();
	}

	
	/**
	 * To read the choice from the user
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String readMessage() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the message :: ");
		System.out.println();
		String s = br.readLine();
		return s;
	}
	
	/**
	 * To read the choice from the user
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String readChoice() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		if(!(s.equals("Y")||s.equals("y")||s.equals("n")||s.equals("N"))){
			System.out.println("Enter either Y or N");
			readChoice();
		}
		return s;
	}
	
	/**
	 * To read the choice from the user
	 * 
	 * @return
	 */
	public static int readOffset() {
		@SuppressWarnings("resource")
		Scanner choiceScanner = new Scanner(System.in);
		int i = 1;
		int choice = 0;
		System.out.println("Enter the offset :: ");
		while (0 != i) {
			if (choiceScanner.hasNextInt()) {
				choice = choiceScanner.nextInt();
				i--;
			} else
				System.out.println("Enter a valid offset");
		}
		return choice;
	}

	/**
	 * Message used for calculating the number of bytes to be appended and appending the message with 1s and 0s.
	 * @param dataSegment
	 * 		input which is to be appended with 1s and 0s.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int getPadCount(String dataSegment) throws UnsupportedEncodingException {

		int padCount = 0;
		int byteCount = ProjectUtility.getByteCount(dataSegment);//Return the number if bytes to be appeded.
		if (byteCount > 252) {
			int rem = byteCount % 252;
			if (rem > 0) {
				padCount = 252 - rem;
			}
		} else {
			padCount = 252 - byteCount;
		}
		System.out.println("Padding count = " + padCount);
		return padCount;

	}

	/**
	 * Retrieving the sequence from the message,ie. the first 4 bytes of the message. 
	 * @param msg
	 * 		Input message.
	 * @return
	 */
	public static int getSeqFromMessage(String msg) {

		String seq = msg.substring(0, 4);
		int seqCount = Integer.parseInt(seq);
		return seqCount;
	}

	/**
	 * Returns the number of bytes of the message.
	 * @param dataSegment
	 * 		Input message
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int getByteCount(String dataSegment) throws UnsupportedEncodingException {
		int byteCount = 0;
		if (null != dataSegment && !dataSegment.isEmpty()) {
			final byte[] utf8Bytes = dataSegment.getBytes("UTF-8");
			byteCount = utf8Bytes.length;
		} else {
			System.out.println("Data Segment is empty");
		}
		return byteCount;
	}

	/**
	 * Method used for padding the message with 1s ad 0s
	 * @param dataSegment
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String padMessage(String dataSegment) throws UnsupportedEncodingException {

		 padCount = getPadCount(dataSegment);
		if (padCount > 0) {
			dataSegment = dataSegment.concat("1");//initially padding 1  
			for (int i = 1; i < padCount; i++) {
				dataSegment = dataSegment.concat("0"); //padding 0s.
			}
		}
		return dataSegment;
	}

	/**
	 * Method used for padding bits for doing the hash operation.
	 * @param message
	 * 		Input message.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String padBitsForHashing(String message) throws UnsupportedEncodingException {

		int rem = 0;
		int padCount = 0;
		int lengthOfMsg = message.getBytes("UTF-8").length;
		String outputMessage = new String();
		outputMessage = message.toString();
		outputMessage = outputMessage.concat("1"); // Padding done at least once
		int lengthOfUnpadedMsg = (lengthOfMsg + 8 + 1)*8; //64 is the size of the field L and 8 will be the size of the 1 that was appended
		if (lengthOfUnpadedMsg > 512) {
			rem = lengthOfUnpadedMsg % 512;
			if (rem > 0) {
				padCount = 512 - rem;
			}

		} else {
			padCount = 512 - lengthOfUnpadedMsg;
		}
		for (int i = 0; i < padCount;i += 8) {
			outputMessage  = outputMessage .concat("0"); 
		}
		outputMessage  = outputMessage .concat(String.format("%08d", lengthOfMsg));//representing length of message into a 64bit value
		return outputMessage ;

	}

	/**
	 * Method for converting string in to int[]
	 * @param s
	 * 		String to be converted.
	 * @return
	 */
	public static int[] toIntArray(String s) {
		String[] stringArray = s.split("(?!^)");
		int[] intArray = new int[stringArray.length];
		int i = 0;
		for (String string2 : stringArray) {
			int decimal = Integer.parseInt(string2, 16);
			intArray[i] = decimal;
			i++;
			// System.out.println("Hex value is " + decimal);
			// System.out.println(string2);
		}
		return intArray;
	}

	/**
	 * Method used for generating the hash value.
	 * @param plainText
	 * @param offset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] generateHashValue(String plainText, int offset) throws UnsupportedEncodingException {
		
		String paddedPlainText = padBitsForHashing(plainText); //Padding the bits.
		String[] arrayOfPaddeMsg = Divider.divide(paddedPlainText);//Dividing the padded message to messages of size 512bit
		byte[] M1 = arrayOfPaddeMsg[0].getBytes();
		RC4 rc4Utility = new RC4(M1, false);
		byte[] state = rc4Utility.KeySchedule(M1);
		byte[] StateM1 = rc4Utility.prgaStar(state, offset);//Performing PRGA operation.
		byte[] compressedOutput = Compressor.compress(StateM1, arrayOfPaddeMsg,0, false,offset,rc4Utility);
	//	System.out.println(" compressedOutput size :: "+compressedOutput.length);
		byte[] afterFirstOutputStep = ProjectUtility.firstOutputStep(compressedOutput);
	//	System.out.println(" afterFirstOutputStep size :: "+afterFirstOutputStep.length);
		byte[] afterSecondOutputStep = ProjectUtility.secondOutputStep(afterFirstOutputStep);
	//	System.out.println(" afterSecondOutputStep size :: "+afterSecondOutputStep.length);
		return afterSecondOutputStep;

	}

	/**
	 * This performs the first step for generating the output
	 * @param S
	 * @return
	 */
	public static byte[] firstOutputStep(byte[] S){
		
		RC4 rc4 = new RC4(S,false);
		byte[] state = rc4.KeySchedule(S);
		byte[] prgaOutput = rc4.PRGA(state, 512);
		byte[] bufferNew = new byte[256];
		bufferNew = Arrays.copyOfRange(prgaOutput, 256, 512); //Retrieving the packet after removing the 4 byte sequence value.
		return bufferNew;
	}
	
	/**
	 * Gets the last bit of each byte value in the odd position and creates a 128bit value by combining them. 
	 * @param S
	 * @return
	 */
	public static byte[] secondOutputStep(byte[] S){
		StringBuffer string = new StringBuffer();
		for (int i = 1; i < S.length; i+=2) {
			string.append(getBit(S[i],7));
		}
		byte[] bval = new BigInteger(string.toString(), 2).toByteArray();
		if(bval.length > 16){
			return Arrays.copyOfRange(bval,1,bval.length);
		}else{
			return bval;
		}
		
	}
	/**
	 * Message to read the hexadecimal key.
	 * @return
	 * 		returns the read key.
	 * @throws IOException
	 */
	public static String readKey() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the key :: ");
		System.out.println();
		String s = br.readLine();
		return s;

	}
	
	/**
	 * returns 1 if right most bit is 1 , otherwise returns 0
	 * @param b
	 * @param position
	 * @return
	 */
	public static int getBit(byte b,int position)
	{
	   return ((b >>  position) & 1);
	}

	/**
	 * Method for converting the hexadecimal string input to array of bytes.
	 * @param s
	 * 		input hexadecimal string
	 * @return
	 * 		byte array of input string.
	 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts int array to hexadecimal string
	 * @param intArray
	 * @return
	 */
	public static String intArrayToHexString(int[] intArray) {

		StringBuffer string = new StringBuffer();
		for (int i : intArray) {
			string.append(Integer.toHexString(i));
		}
		return string.toString();
	}
	
	/**
	 * Method used while creating hash function. Returns the value for len.
	 * @param msg
	 * 		input message.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int calculateLen(String msg) throws UnsupportedEncodingException{
		byte[] bytes = msg.getBytes("UTF-8");
		int len = 0;
		for (Byte b : bytes) {
			len = len + b.intValue();
		}
		return len;
	}
}
