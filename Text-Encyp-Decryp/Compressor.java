package com.sample.projectsample;

import java.io.UnsupportedEncodingException;

/**
 * @author Sanjith
 * 
 * Class for doing the Compression process while generating the Hash function 
 *
 */
public class Compressor {

	/**
	 * Method for doing compression operation.
	 * @param State
	 * 			State using which compression is to be done.
	 * @param arrayOfPaddeMsg
	 * 			Input padded message
	 * @param index
	 * 			variable used for accessing the message from the message array 
	 * @param isKSARequired
	 * 			specifies whether KSA call required
	 * @param offset
	 * 			the offset value
	 * @return
	 * 			the byte array after doing the compression operation.
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] compress(byte[] State, String[] arrayOfPaddeMsg, int index, boolean isKSARequired,int offset,RC4 rc4Utility) throws UnsupportedEncodingException {
		byte[] newState,output;
		if (isKSARequired) {
			newState = RC4.ksaStar(arrayOfPaddeMsg[index].getBytes(), State);
		}else{
			newState = State;
		}
		int len = ProjectUtility.calculateLen(arrayOfPaddeMsg[index]);
		if(len%256 != 0)
			output = rc4Utility.prgaStar(newState, len%256);
		else
			output = rc4Utility.prgaStar(newState, offset);
		if(index+1 < arrayOfPaddeMsg.length)	
			Compressor.compress(output, arrayOfPaddeMsg, index+1, true, offset,rc4Utility);
		
		return output;
	}
}
