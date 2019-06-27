//
// Trivium stream cipher (inefficient emulator)
//
public class Trivium {
	private boolean[] A, B, C;
	
	// Constructor
	public Trivium(boolean[] key, boolean[] iv) throws Exception {
		// Create registers
		this.A = new boolean[93];
		this.B = new boolean[84];
		this.C = new boolean[111];
		
		// Check key and initialisation vector lengths
		if (key.length!=80 || iv.length!=80) {
			throw new Exception("The key and the initialisation vector must each contain 80 elements");
		}
		
		// Initialise registers
		System.arraycopy(iv, 0, A, 0, 80);
		java.util.Arrays.fill(A, 80, A.length, false);
		System.arraycopy(key, 0, B, 0, 80);
		java.util.Arrays.fill(B, 80, B.length, false);
		java.util.Arrays.fill(C, 0, C.length-3, false);
		C[108] = C[109] = C[110] = true;
		
		// Warm-up phase: discard the first 1152 bits of the key stream
		for (int i=0; i<1152; i++) {
			this.step();
		}
	}
	
	// Step method: advance registers and return the next key stream bit
	private boolean step() {
		// Calculate key stream bit
		boolean resultA;
		boolean resultB;
		boolean resultC;
		
		// Calculate new leftmost bits
		boolean nextLeftmostBitA = A[68] ^ resultC;



		
		// Update the three registers 



		
		// Return the next key stream bit


	}
	
	// Encryption/decryption method
	public byte[] encrypt(byte[] input) {
		byte[] result = new byte[input.length];
		
		// Encrypt/decrypt 8 bits at a time
		for (int i=0; i<input.length; i++) {
			// Get one byte of key stream bits (i.e. 8 bits)
			byte key = 0;
			for (int j=0; j<8; j++) {
				key = (byte) ((key<<1) + (this.step()?1:0));
			}
			// Encrypt/decrypt
			result[i] = (byte)(input[i] ^ key);
		}
		
		// Return result
		return result;
	}	
}
