package com.sample.projectsample;


/**
 * @author Sanjith
 * Class used for doing the RC4 encryption, decryption, IPRGA, PRGA and KSA.
 */
public class RC4 {
	private static final byte[] S = new byte[256];
	private static int i=0,j=0;
	private final byte[] T = new byte[256];
	private int keylen;

	/**
	 * Constructor of RC4 class. Does KSA on the argument key.
	 * @param key
	 * @param keySchedulingReq
	 */
	public RC4(final byte[] key, boolean keySchedulingReq) {
		if (key.length < 1 || key.length > 256) {
			throw new IllegalArgumentException("key must be between 1 and 256 bytes");
		} else {
			if (keySchedulingReq) {
				keylen = key.length;
				for (int i = 0; i < 256; i++) {
					S[i] = (byte) i;
					T[i] = key[i % keylen];
				}
				int j = 0;
				byte tmp;
				for (int i = 0; i < 256; i++) {
					j = (j + S[i] + T[i]) & 0xFF;
					tmp = S[j];
					S[j] = S[i];
					S[i] = tmp;
				}

			}
		}

	}

	/**
	 * Method for doing the key scheduling. It is a utility method
	 * 
	 * @param key
	 * @return
	 */
	public byte[] KeySchedule(final byte[] key) {
		byte[] S = new byte[256];
		if (key.length < 1 || key.length > 256) {
			throw new IllegalArgumentException("key must be between 1 and 256 bytes");
		} else {
				keylen = key.length;
				for (int i = 0; i < 256; i++) {
					S[i] = (byte) i;
					T[i] = key[i % keylen];
				}
				int j = 0;
				byte tmp;
				for (int i = 0; i < 256; i++) {
					j = (j + S[i] + T[i]) & 0xFF;
					tmp = S[j];
					S[j] = S[i];
					S[i] = tmp;
				}

		}
		return S;

	}
	
	public byte[] getS() {
		return S;
	}

	/**
	 * Method for doing the encryption using PRGA.
	 * @param plaintext
	 * 			input string to be encrypted
	 * @return
	 */
	public byte[] encrypt(final byte[] plaintext) {
		final byte[] ciphertext = new byte[plaintext.length];
		int i = this.i, j = this.j, k, t;
		byte tmp;
		for (int counter = 0; counter < plaintext.length; counter++) {
			i = (i + 1) & 0xFF;
			j = (j + S[i]) & 0xFF;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
			t = (S[i] + S[j]) & 0xFF;
			k = S[t];
			ciphertext[counter] = (byte) (plaintext[counter] ^ k);
			
		}
		this.i = i;
		this.j=j;
		return ciphertext;
	}

	/**
	 * Method that does PRGA.
	 * @param plaintext
	 * @return
	 */
	public  byte[] PRGA(byte[] buffer) {
		// final byte[] ciphertext = new byte[plaintext.length];
		int k, t;
		byte tmp;
		int i=this.i,j=this.j;
		for (int counter = 0; counter < buffer.length; counter++) {
			i = (i + 1) & 0xFF;
			j = (j + S[i]) & 0xFF;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
			t = (S[i] + S[j]) & 0xFF;
			// ciphertext[counter] = (byte) (plaintext[counter] ^ k);
		}
		this.i = i;
		this.j = j;
		return S;
	}
	
	/**
	 * Method that does PRGA.
	 * @param plaintext
	 * @return
	 */
	public  byte[] PRGA(byte[] S,int length) {
		// final byte[] ciphertext = new byte[plaintext.length];
		int k, t;
		byte tmp;
		byte[] prgaOutputState = new byte[length];
		int i=0,j=0;
		for (int counter = 0; counter < length; counter++) {
			i = (i + 1) & 0xFF;
			j = (j + S[i]) & 0xFF;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
			t = (S[i] + S[j]) & 0xFF;
			prgaOutputState[counter] = S[t];
			// ciphertext[counter] = (byte) (plaintext[counter] ^ k);
		}
		return prgaOutputState;
	}

	/**
	 * Method that performs IPRGA
	 * @param buffer
	 * 		Input message in the byte array format.
	 * @return
	 */
	public  byte[] IPRGA(byte[] buffer) {

		byte tmp;
		int i = this.i,j=this.j;
		for (int counter = 0; counter < buffer.length; counter++) {
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
			j = (j - S[i] + 256) & 0xFF;
			i = (i - 1) & 0xFF;

		}
		this.i = i;
		this.j = j;
		return S;
	}

	/**
	 * Decryption method. Delegates the call to the encrypt method.
	 * @param ciphertext
	 * @return
	 */
	public byte[] decrypt(final byte[] ciphertext) {
		return encrypt(ciphertext);
	}
	
	/**
	 * Does KSA operation on the input byte array and the message
	 * @param M
	 * 		Input message
	 * @param S
	 * 		Input state
	 * @return
	 */
	public static byte[] ksaStar(byte[] M,byte[] S){
		int j=0;
		byte tmp;
		for (int i = 0; i < 256; i++) {
			j=(j+S[i]+M[i%40])& 0xFF;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
		}
		return S;
	}
	
	/**
	 * Method for doing the PRGA operation. 
	 * @param S
	 * 		Input state
	 * @param length
	 * 		Specifies the number of rounds required.
	 * @return
	 */
	public  byte[] prgaStar(byte[] S,int length) {
		byte tmp;
		int j=0;
		for (int i = 0; i< length; i++) {
			i = (i + 1) & 0xFF;
			j = (j + S[i]) & 0xFF;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
		}
		return S;
	}
}
