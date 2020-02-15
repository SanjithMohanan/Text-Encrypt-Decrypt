package com.sample.projectsample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sanjith
 * 
 *         The Receiver class. This class contains the main class for running
 *         the receiver.
 *
 */
class DatagramReceiver {

	private static int currentSeq = 0; // variable to store the current
										// sequence. Initialized to 0.
	public static String key = new String();
	public static StateMetadata currentStat; // variable for storing the current
												// state. Will contain the value
												// for i and j.
	private static RC4 rc4; // The RC4 instance.
	private static Map<Integer,String> map = new TreeMap<Integer,String>();

	public static void main(String[] args) throws IOException {
		key = ProjectUtility.readKey(); // reading the key from the receiver
										// side.Reads hexadecimal key.
		int offset =  ProjectUtility.readOffset();
		currentStat = new StateMetadata(ProjectUtility.toIntArray(key), 0, 0); // storing
																				// the
																				// key
																				// in
																				// int[]
																				// format.
		String key = ProjectUtility.intArrayToHexString(currentStat.getCurrentState()); //
		byte[] keyBytes = ProjectUtility.hexStringToByteArray(key); // Converting
																	// the
																	// hexadecimal
																	// key to
																	// byte[]
		rc4 = new RC4(keyBytes, true); // initializing the RC4 instance. The key
										// scheduling will be done with in the
										// constructor for this class.

		int MAX_LEN = 272; // Setting the maximum length of package to 272.
		int localPortNum = Integer.parseInt("2389");// variable which specifies
													// the local port.
		DatagramSocket mySocket = new DatagramSocket(localPortNum); // Initializing
																	// the
																	// socket
																	// for
																	// communication.
		byte[] buffer = new byte[MAX_LEN]; // Initializing the buffer that
		// receives the packet.
		
		
		InetAddress receiverHost = InetAddress.getByName("localhost");
		int receiverPort = Integer.parseInt("2390");
		
		DatagramSocket myReceiverSocket = new DatagramSocket();

		try {
			while (true) { // Always receiving the packet send from the sender.

				DatagramPacket packet = new DatagramPacket(buffer, MAX_LEN); // Initializing
																				// the
																				// packet
																				// of
																				// length
																				// MAX_LEN.
				mySocket.receive(packet); // Receiving the packet.
				String message = new String(buffer);

				byte[] bufferNew = new byte[268];
				bufferNew = Arrays.copyOfRange(buffer, 4, buffer.length); // Retrieving
																			// the
																			// packet
																			// after
																			// removing
																			// the
																			// 4
																			// byte
																			// sequence
																			// value.

				
				String seq = message.substring(0, 4); // Retrieving the sequence
														// from the message. ie
														// the first 4 bytes.
				if(seq.equals("done")){
					int padCount = Integer.parseInt(message.substring(4,8));
					System.out.println("The reassembled received data is : ");
					for (int i=0; i<map.size()-1; i++) {
						if(null != map.get(i)){
							System.out.print(map.get(i));
						}
					}
					String string = map.get(map.size()-1);
					System.out.println(string.substring(0, string.length()-padCount));
					System.exit(0);
				}
				System.out.println("Receiving packets.....");
				System.out.println("seq :: " + seq);
				processKey(rc4, seq, bufferNew); // Processing the received
													// message.

				key = ProjectUtility.intArrayToHexString(currentStat.getCurrentState()); // getting
																							// the
																							// current
																							// state.
				keyBytes = ProjectUtility.hexStringToByteArray(key);// Converting
																	// it in to
																	// byte
																	// array.

				byte[] decipher = rc4.decrypt(bufferNew); // decrypting the
															// message

				String decipherString = new String(decipher, "UTF-8");
				System.out.println("sequence :: " + seq);// Printing the
															// sequence
				System.out.println("decipherString :: " + decipherString.substring(0, 252).toString());// Printing
																						// the
																						// message
				String outputCipher = decipherString.substring(0, 252).toString();
					map.put(currentSeq-1,outputCipher);
				String dataSegment = decipherString.toString().substring(0, 252);
				byte[] receivedString = Arrays.copyOfRange(decipher, 252, decipher.length);
				byte[] receiverSideHashValue = ProjectUtility.generateHashValue(seq + dataSegment, offset);
				System.out.println("Receiver side hash value = " + Arrays.toString(receiverSideHashValue));
				System.out.println("Received hash value = " + Arrays.toString(receivedString));
				if (Arrays.equals(receivedString, receiverSideHashValue)) {
					System.out.println("Both hash is equal. Decrypted succesfully");
				} else {
					System.out.println("Hash value do not match. Do you want the whole message to be resend(Y/N) ?");
					String choice = ProjectUtility.readChoice();
					if(choice.equals("Y")||choice.equals("N")){
						byte[] receiverBuffer = "Resend packet".getBytes();
						DatagramPacket packetToSender = new DatagramPacket(receiverBuffer, receiverBuffer.length, receiverHost,
								receiverPort);
						mySocket.send(packetToSender);

					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.out.println("The reassembled received data is : ");
			for (int i=0; i<map.size(); i++) {
				if(null != map.get(i)){
					System.out.println(map.get(i));
				}
			}
		}
		// }
	}

	/**
	 * Method responsible for counting the number of PRGA/IPRGA rounds required
	 * and executing the PRGA/IPRGA rounds and updating the value for the
	 * sequence.
	 * 
	 * @param rc4
	 *            REc instance
	 * @param seq
	 *            sequence from sender
	 * @param buffer
	 *            received encrypted message in the buffer
	 * @throws UnsupportedEncodingException
	 */
	public static void processKey(RC4 rc4, String seq, byte[] buffer) throws UnsupportedEncodingException {
		int seqCount = Integer.parseInt(seq); // Converting the string sequence
												// in to integer value.
		System.out.println("current seq : " + currentSeq);
		int reqRounds = getRequiredRounds(currentSeq, seqCount); // Counting the
																	// number of
																	// PRGA/IPRGA
																	// rounds
																	// required.
		if (reqRounds != 0) {
			if (reqRounds > 0) {
				System.out.println("PRGA forward rounds : " + reqRounds + "*268");
				for (int i = 0; i < reqRounds; i++) {
					rc4.PRGA(buffer); // Performing PRGA rounds
				}

			} else {
				System.out.println("IPRGA forward rounds : " + -reqRounds + "*268");
				for (int i = 0; i < -reqRounds; i++) {
					rc4.IPRGA(buffer);// Performing IPRGA rounds
				}

			}
		}
		currentSeq = seqCount + 1; // Updating the current sequence
	}

	/**
	 * Method responsible for counting the number of rounds required.
	 * 
	 * @param currentSequence
	 *            Current sequence
	 * @param seqCount
	 *            sequence from sender.
	 * @return
	 */
	public static int getRequiredRounds(int currentSequence, int seqCount) {

		return seqCount - currentSequence;
	}

}
