//
// Simple test for the Trivium emulator
//
public class TriviumTest {
	
	// Key and initialisation vector for Trivium
	static private boolean[] key = stringToBoolean("Secret key");
	static private boolean[] iv = stringToBoolean("Nonce");

	// Test method
	public static void main(String[] args) throws Exception {
		// "Send" a secret message
		byte[] ciphertext = send("This is a secret message");
		
		// Decrypt the ciphertext
		receive(ciphertext);
	}

	// Sender
	static private byte[] send(String message) throws Exception {
		// Instantiate Trivium cipher
		Trivium cipher = new Trivium(key, iv);
		
		// Print original message
		System.out.println("Original message:    " + message);
		
		// Convert message to byte array
		byte[] plaintext = message.getBytes();
		System.out.println("Plaintext:           " + javax.xml.bind.DatatypeConverter.printHexBinary(plaintext));
		
		// Encrypt message
		byte[] ciphertext = cipher.encrypt(plaintext);
		System.out.println("Ciphertext:          " + javax.xml.bind.DatatypeConverter.printHexBinary(ciphertext));		
		
		// Return ciphertext
		return ciphertext;
	}
	
	// Receiver
	static private void receive(byte[] ciphertext) throws Exception {
		// Instantiate Trivium cipher (use same key and initialisation vector as the sender)
		Trivium cipher = new Trivium(key, iv);
		
		// Decrypt
		byte[] decryptedPlaintext = cipher.encrypt(ciphertext);
		System.out.println("Decrypted plaintext: " + javax.xml.bind.DatatypeConverter.printHexBinary(decryptedPlaintext));
		
		// Convert decrypted message to String
		String decryptedMessage = new String(decryptedPlaintext);
		System.out.println("Decrypted message:   " + decryptedMessage); 
	}

	// Auxiliary function for generating 80-element key and initialisation vector
	private static boolean[] stringToBoolean (String s) {
		boolean[] result = new boolean[80];
		char[] x = s.toCharArray();	
		for (int i=0; i<10; i++) {
			byte b = (byte)((i<x.length)?x[i]:0);
			for (int j=0; j<8; j++) {
				result[8*i+j] = (b%2)==1;
				b >>= 1;
			}
		}
		return result;
	}
}
