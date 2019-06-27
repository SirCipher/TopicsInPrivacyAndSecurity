
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESvsRSA {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
	    // Create large random plaintext
	    byte[] plaintext = new byte[1000000];
	    Random rnd = new Random();
	    rnd.nextBytes(plaintext);
	    	    
		// Create and initialise AES and RSA ciphers for encryption
		Cipher aesEncryptCipher = initAES();
		Cipher rsaEncryptCipher = initRSA();
	    
	    // Test AES cipher
	    long startTime, endTime, duration;
	    startTime = System.nanoTime();
	    encrypt(aesEncryptCipher, plaintext);
	    endTime = System.nanoTime();
	    duration = endTime - startTime;
	    System.out.println("AES encryption duration: " + duration + " ns"); 
	    
	    // Test RSA cipher
	    startTime = System.nanoTime();
	    encrypt(rsaEncryptCipher, plaintext);
	    endTime = System.nanoTime();
	    duration = endTime - startTime;
	    System.out.println("RSA encryption duration: " + duration + " ns"); 
	}
		
	// Create AES cipher and initialise it for encryption
	private static Cipher initAES() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
	    // Generate random 128-bit AES (session) key
 		KeyGenerator kgen = KeyGenerator.getInstance("AES");
 	    kgen.init(128);
 	    SecretKey key = kgen.generateKey();

 	    // Create an initialisation vector
	    IvParameterSpec iv = new IvParameterSpec(new byte[] {123, 21, 45, 73, 123, 21, 45, 73, 123, 21, 45, 73, 123, 21, 45, 73});
		
	    // Create and return AES encryption cipher
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
	    return cipher;
	}
	
	// Create RSA cipher and initialise it for encryption
	private static Cipher initRSA() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException, NoSuchProviderException {
	    // Generate RSA public and private keys
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		PublicKey publicKey = pair.getPublic();
		
	    // Create and return RSA encryption cipher
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	    return cipher;
	}

	// Encryption
	private static byte[] encrypt(Cipher cipher, byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
		byte[] plaintextBlock = new byte[100];
		byte[] ciphertextBlock;
		byte[] ciphertext = new byte[2*plaintext.length];
		
		for (int i=0, pos=0; i<plaintext.length/100; i++) {
			System.arraycopy(plaintext, 100*i, plaintextBlock, 0, 100);
			ciphertextBlock = cipher.doFinal(plaintextBlock);
			System.arraycopy(ciphertextBlock, 0, ciphertext, pos, ciphertextBlock.length);
			pos += ciphertextBlock.length;
		}
		
		return ciphertext;
	}
}
