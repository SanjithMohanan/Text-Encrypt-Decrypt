package com.sample.projectsample;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * @author Sanjith
 * Sender class. Contains  main class for running the Sender.
 *
 */
class DatagramSender {

	private static String key = new String();
	private static RC4 rc4; //RC4 instance.

	public static void main(String[] args) {

		// while(true){
		try {
			InetAddress receiverHost = InetAddress.getByName("localhost");
			int receiverPort = Integer.parseInt("2389"); //Receiver port.
			DatagramSocket mySocket = new DatagramSocket();//Socket for data communication.

			key = ProjectUtility.readKey(); //Reading the hexadecimal string key.
			
			byte[] keyBytes = ProjectUtility.hexStringToByteArray(key); //Converting the key in to byte array.
			rc4 = new RC4(keyBytes, true); // key scheduling done with in the
												// constructor.
			
			int offset = ProjectUtility.readOffset();
			String inputMessage = ProjectUtility.readMessage();//Reading the message
			String paddedInputMsg = ProjectUtility.padMessage(inputMessage); //Padding the message.
			String[] msgArray = ProjectUtility.splitMessage(paddedInputMsg);//Splitting the message in to messages of size 252.
			int packetCount = paddedInputMsg.length()/252;//represents the number of packets going to be send.
			int[] sequenceOrder = ProjectUtility.readSequenceOrder(packetCount); //Reading the sequence in which the packet need to be send from the user.
			

			
			int localPortNum = Integer.parseInt("2390");
			
			DatagramSocket myReceiverSocket = new DatagramSocket(localPortNum);
			
			while (true) {

				byte[][] arrayOfPackets = new byte[msgArray.length][272]; //2D array stores the array of packets.
				int order=0;//the sequence number in senders side. Initial value is 0.
				for (String msg : msgArray) {//for each message
					
					String stringSequence = String.format("%04d", order);//Converting the sequence number in to string format of size 4 bytes.
					byte[] buffer = prepareDataSegment( msg,stringSequence, key, offset); //preparing and appending the hash value and encryption is done in this method.
					
					byte[] one = stringSequence.getBytes();//Combining the sequence and the encrypted message.
					byte[] two = buffer;
					byte[] combined = new byte[one.length + two.length];
					for (int i = 0; i < combined.length; ++i)
					{
					    combined[i] = i < one.length ? one[i] : two[i - one.length];
					}
					arrayOfPackets[order] = combined;//Array of encrypted message.
					order++;
					/*DatagramPacket packet = new DatagramPacket(combined, combined.length, receiverHost, receiverPort);
					mySocket.send(packet);*/
				}
				
				int a = 0;
				DatagramPacket packet = null;
				/**
				 * Sending the message in the order got from the user. 
				 */
				for (int seq : sequenceOrder) {
					System.out.println("Sending the packet of sequence :: " + (seq-1));
					if(a == 0)
						packet = new DatagramPacket(arrayOfPackets[seq-1], arrayOfPackets[seq-1].length, receiverHost, receiverPort);
					else
						packet.setData(arrayOfPackets[seq-1]);
					a++;
					mySocket.send(packet);//Sending the packet
				}
				String pad = "done" + String.format("%04d", ProjectUtility.padCount);
				packet.setData(pad.getBytes());
				mySocket.send(packet);

				int MAX_LEN = 40;
				byte[] receiverBuffer = new byte[MAX_LEN];
				DatagramPacket packetFromReceiver = new DatagramPacket(receiverBuffer, MAX_LEN);
				myReceiverSocket.receive(packetFromReceiver);
				String messageFromReceiver = new String(receiverBuffer);
				System.out.println("From receiver : " + messageFromReceiver);
				if(!messageFromReceiver.trim().equals("Resend packets")){
					System.out.println("Message passing succesful. Terminating progam.....");
					System.exit(0) ;
				}
				

				//mySocket.close();
				// TimeUnit.MINUTES.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method responsible for creating and appending the hash function and encrypting the message.
	 * @param plainText
	 * 			The plain text.
	 * @param key
	 * 			The key read from the user.
	 * @param offset
	 * 			The offset value
	 * @return
	 * 			The encrypted data in byte[] format.
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] prepareDataSegment(String plainText,String sequence, String key, int offset)
			throws UnsupportedEncodingException {

		byte[] one = plainText.getBytes();//Combining the sequence and the encrypted message.
		byte[] two = ProjectUtility.generateHashValue(sequence+plainText, offset);;
		byte[] plainTextBytes = new byte[one.length + two.length];
		for (int i = 0; i < plainTextBytes.length; ++i)
		{
			plainTextBytes[i] = i < one.length ? one[i] : two[i - one.length];
		}
		
		String aftergetbyted = new String(plainTextBytes);
		System.out.println("Packets to be send are = " + aftergetbyted.substring(0,252).toString());
		System.out.println("Hash value for the packet = " + Arrays.toString(two));
		int packetSize = aftergetbyted.substring(0,252).length() + two.length + sequence.length();
		System.out.println("Packet size = " + packetSize);
		byte[] cipher = rc4.encrypt(plainTextBytes);//Encrypting the message.
		return cipher;
	}
}