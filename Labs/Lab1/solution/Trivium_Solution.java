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
		boolean resultA = A[65] ^ (A[90] && A[91]) ^ A[92];
		boolean resultB = B[68] ^ (B[81] && B[82]) ^ B[83];
		boolean resultC = C[65] ^ (C[108] && C[109]) ^ C[110];
		
		// Calculate new leftmost bits
		boolean nextLeftmostBitA = A[68] ^ resultC;
		boolean nextLeftmostBitB = B[77] ^ resultA;
		boolean nextLeftmostBitC = C[86] ^ resultB;
		
		// Update the three registers 
		System.arraycopy(A, 0, A, 1, A.length-1);
		A[0] = nextLeftmostBitA;
		System.arraycopy(B, 0, B, 1, B.length-1);
		B[0] = nextLeftmostBitB;
		System.arraycopy(C, 0, C, 1, C.length-1);
		C[0] = nextLeftmostBitC;
		
		// Return the next key stream bit
		return resultA ^ resultB ^ resultC;
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
