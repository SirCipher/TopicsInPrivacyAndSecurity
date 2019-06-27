public class Steganography extends Steganography_Abstract {	
	/*
	 * Inserts the secret text into the image byte array, one bit at a time
	 * @param image The original image byte array
	 * @param text The text byte array (containing the text length in the first 4 bytes)
	 */
	@Override
	protected void insert_hidden_text(byte[] image, String text) {
		// Obtain byte-array buffer containing the length of the text (4 bytes) followed by the text
		byte[] buffer = text2byteArray(text);
		
		// Check that the image contains at least one byte for each bit of secret text
		if (8 * buffer.length > image.length) {
			throw new IllegalArgumentException("File not long enough!");
		}

		// Iterate through each buffer byte and place its bits into 8 consecutive image bytes

		
		
		
		
		
		
	}

	/*
	 * Retrieves the text hidden in a steganographic image
	 * @param image The byte array containing the steganographic image
	 * @return The byte array containing the hidden text
	 */
	@Override
	protected byte[] extract_hidden_text(byte[] image) {
		int length = 0;
		
		// Iterate through the first 32 bytes of image data to extract text length

		
		
		
		
		// Allocate space for the secret text
		byte[] text = new byte[length];
		
		// Extract the length bytes of text, each from 8 consecutive image bytes
		
		
		
		


		
		// Return the secret text
		return text;
	}
	
	/* 
	 * Generates byte buffer containing the secret message preceded by its length
	 * @param text The secret message 
	 * @return The byte buffer containing the length of the message followed by the message
	 */
	private byte[] text2byteArray(String text) {
		// Create byte buffer comprising 4 bytes for the text length followed by the text
		int len = text.length();		
		byte[] buffer = new byte[len + 4];
		
		// Extract and place the 4 buffers of the text length into the buffer
		buffer[0] = (byte)((len >>> 24) & 0xFF);
		buffer[1] = (byte)((len >>> 16) & 0xFF);
		buffer[2] = (byte)((len >>> 8) & 0xFF);
		buffer[3] = (byte)(len & 0xFF);

		// Copy the bytes of text into the buffer, after the 4 bytes containing the text length
		System.arraycopy(text.getBytes(), 0, buffer, 4, len);
		return buffer;
	}
}

