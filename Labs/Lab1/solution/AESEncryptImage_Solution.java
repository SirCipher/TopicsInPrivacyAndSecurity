import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;

import javax.crypto.spec.IvParameterSpec;

public class AESEncryptImage {
	// PNG image file path and name
	private static String path = "ADD-PATH-TO-IMAGE-FILES-HERE";
	private static String imageFileName = "house.jpg";

	// Main method
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
	    // Generate random 128-bit AES (session) key
 		KeyGenerator kgen = KeyGenerator.getInstance("AES");
 	    kgen.init(128);
 	    SecretKey key = kgen.generateKey();
 	    
 	    // Create an initialisation vector
	    IvParameterSpec iv = new IvParameterSpec(new byte[] {123, 21, 45, 73, 123, 21, 45, 73, 123, 21, 45, 73, 123, 21, 45, 73});
 	    
 	    // Encrypt image
 	    encrypt(key, iv, imageFileName);
 	    
		// Decrypt image
 	    decrypt(key, iv, imageFileName);

 	    // Finish
		System.out.println("Done");
	}

	// Encryption
	private static void encrypt(SecretKey key, IvParameterSpec iv, String imageFileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// Read image file
		byte[] imageBytes = readImageFile(path + imageFileName);
 	    
		// Instantiate AES cipher and initialise it for encryption
	    Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.ENCRYPT_MODE, key, iv);
		
		// Encrypt plaintext and use ciphertext to overwrite image
		byte[] ciphertext = aesCipher.doFinal(imageBytes);
		System.arraycopy(ciphertext, 0, imageBytes, 0, imageBytes.length);
		
		// Write encrypted image file
		writeImageFile(path + "encrypted-" + imageFileName);
	}
	
	// Decryption
	private static void decrypt(SecretKey key, IvParameterSpec iv, String imageFileName) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException {
		// Read encrypted image file
		byte[] imageBytes = readImageFile(path + "encrypted-" + imageFileName);

		// Instantiate AES cipher and initialise it for decryption
		Cipher aesCipher = Cipher.getInstance("AES/CBC/NoPadding");
		aesCipher.init(Cipher.DECRYPT_MODE, key, iv);
		
		// Decrypt ciphertext and use plaintext to overwrite image
		byte[] plaintext = aesCipher.doFinal(java.util.Arrays.copyOf(imageBytes, imageBytes.length-(imageBytes.length%16)));
		System.arraycopy(plaintext, 0, imageBytes, 0, plaintext.length);
		
		// Write decrypted image file
		writeImageFile(path + "decrypted-" + imageFileName);
	}

	// Auxiliary fields and methods 
	private static BufferedImage img;
	
	private static byte[] readImageFile(String fileName) throws IOException {
		img = ImageIO.read(new File(fileName));
		WritableRaster raster = img.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	private static void writeImageFile(String fileName) throws IOException {
		File out = new File(fileName);
		out.delete();
		ImageIO.write(img, "png", out);
	}
}