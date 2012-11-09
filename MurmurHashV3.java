package ie.ucd.murmur;

/**
 * Murmur hash 3.0.
 * 
 * The Murmur hash is a relative fast hash function from
 * https://code.google.com/p/smhasher/ for platforms with efficient
 * multiplication.
 * 
 * This is a re-implementation of the original C++ code plus some additional
 * features.
 * 
 * Public domain.
 * 
 * @author Viliam Holub
 * @version 1.0
 * 
 */
public final class MurmurHashV3 {

	/**
	 * Generates 128 bit hash from byte array with default seed value.
	 * 
	 * @param data byte array to hash
	 * @param length length of the array to hash
	 * @return 128 bit hash of the given string
	 */
	public static long[] hash128( final byte[] data, int length) {
		return hash128( data, 0, length, 0x9ee73d188796670eL);
	}

	/**
	 * Generates 128 bit hash from a string.
	 * 
	 * @param text string to hash
	 * @return 128 bit hash of the given string
	 */
	public static long hash128_64( final String text) {
		return hash128_64( text, 0, text.length(), 0x9ee73d188796670eL);
	}

	/**
	 * Generates 128 bit hash from a string.
	 * 
	 * @param text string to hash
	 * @param salt initial salt
	 * @return 128 bit hash of the given string
	 */
	public static long hash128_64( final String text, long salt) {
		return hash128_64( text, 0, text.length(), salt);
	}

	static int getblock32( byte[] data, int offset, int index) {
		int i4 = offset + index * 4;
		return (data[ i4 + 0] & 0xff) + ((data[ i4 + 1] & 0xff) << 8)
				+ ((data[ i4 + 2] & 0xff) << 16)
				+ ((data[ i4 + 3] & 0xff) << 24);
	}

	static long getblock( byte[] data, int offset, int index) {
		int i8 = offset + index / 8;
		return ((long) data[ i8 + 0] & 0xff)
				+ (((long) data[ i8 + 1] & 0xff) << 8)
				+ (((long) data[ i8 + 2] & 0xff) << 16)
				+ (((long) data[ i8 + 3] & 0xff) << 24)
				+ (((long) data[ i8 + 4] & 0xff) << 32)
				+ (((long) data[ i8 + 5] & 0xff) << 40)
				+ (((long) data[ i8 + 6] & 0xff) << 48)
				+ (((long) data[ i8 + 7] & 0xff) << 56);
	}

	static long getblock( String data, int offset, int index) {
		int i8 = offset + index / 8 / 2;
		return ((long) data.charAt( i8 + 0) & 0xffff)
				+ (((long) data.charAt( i8 + 1) & 0xffff) << 16)
				+ (((long) data.charAt( i8 + 2) & 0xffff) << 32)
				+ (((long) data.charAt( i8 + 3) & 0xffff) << 48);
	}

	/**
	 * Rotate left.
	 * 
	 * @param val value
	 * @param s shift
	 * @return value rotated left by {@code s}
	 */
	static int rotl32( int val, int s) {
		return ((val << s) | (val >>> (32 - s)));
	}

	/**
	 * Rotate left.
	 * 
	 * @param val value
	 * @param s shift
	 * @return value rotated left by {@code s}
	 */
	static long rotl64( long val, int s) {
		return ((val << s) | (val >>> (64 - s)));
	}

	/**
	 * Finalization mix - force all bits of a hash block to avalanche.
	 * 
	 * @param h value
	 * @return hashed value
	 */
	public static long fmix( long h) {
		h ^= h >>> 33;
		h *= 0xff51afd7ed558ccdL;
		h ^= h >>> 33;
		h *= 0xc4ceb9fe1a85ec53L;
		h ^= h >>> 33;

		return h;
	}

	public static int hash32( byte[] key, int offset, int length, int seed) {
		final int nblocks = length / 4;

		int h1 = seed;

		int c1 = 0xcc9e2d51;
		int c2 = 0x1b873593;

		// Body

		for (int i = 0; i < nblocks; i++) {
			int k1 = getblock32( key, offset, i);

			k1 *= c1;
			k1 = rotl32( k1, 15);
			k1 *= c2;

			h1 ^= k1;
			h1 = rotl32( h1, 13);
			h1 = h1 * 5 + 0xe6546b64;
		}

		// Tail

		int tail = offset + nblocks * 4;

		int k1 = 0;

		switch (length & 3) {
		case 3:
			k1 ^= ((int) key[ tail + 2]) << 16;
		case 2:
			k1 ^= ((int) key[ tail + 1]) << 8;
		case 1:
			k1 ^= ((int) key[ tail]);
			k1 *= c1;
			k1 = rotl32( k1, 15);
			k1 *= c2;
			h1 ^= k1;
		};

		// Finalization

		h1 ^= length;

		return (int) fmix( h1);
	}

	/**
	 * Murmur hash 128 bits.
	 * 
	 * @param data byte array of data
	 * @param offset starting offset
	 * @param length length of data
	 * @param seed initial seed
	 * @return hash in two longs
	 */
	public static long[] hash128( byte[] data, int offset, int length, long seed) {
		final int nblocks = length / 16; // Process as 128-bit blocks.

		long h1 = seed;
		long h2 = seed;

		long c1 = 0x87c37b91114253d5L;
		long c2 = 0x4cf5ad432745937fL;

		// Body

		for (int i = 0; i < nblocks; i++) {
			long k1 = getblock( data, offset, i * 2 + 0);
			long k2 = getblock( data, offset, i * 2 + 1);

			k1 *= c1;
			k1 = rotl64( k1, 31);
			k1 *= c2;
			h1 ^= k1;

			h1 = rotl64( h1, 27);
			h1 += h2;
			h1 = h1 * 5 + 0x52dce729;

			k2 *= c2;
			k2 = rotl64( k2, 33);
			k2 *= c1;
			h2 ^= k2;

			h2 = rotl64( h2, 31);
			h2 += h1;
			h2 = h2 * 5 + 0x38495ab5;
		}

		// Tail

		int tail = offset + nblocks * 16;

		long k1 = 0;
		long k2 = 0;

		switch (length & 15) {
		case 15:
			k2 ^= ((long) data[ tail + 14]) << 48;
		case 14:
			k2 ^= ((long) data[ tail + 13]) << 40;
		case 13:
			k2 ^= ((long) data[ tail + 12]) << 32;
		case 12:
			k2 ^= ((long) data[ tail + 11]) << 24;
		case 11:
			k2 ^= ((long) data[ tail + 10]) << 16;
		case 10:
			k2 ^= ((long) data[ tail + 9]) << 8;
		case 9:
			k2 ^= ((long) data[ tail + 8]) << 0;
			k2 *= c2;
			k2 = rotl64( k2, 33);
			k2 *= c1;
			h2 ^= k2;

		case 8:
			k1 ^= ((long) data[ tail + 7]) << 56;
		case 7:
			k1 ^= ((long) data[ tail + 6]) << 48;
		case 6:
			k1 ^= ((long) data[ tail + 5]) << 40;
		case 5:
			k1 ^= ((long) data[ tail + 4]) << 32;
		case 4:
			k1 ^= ((long) data[ tail + 3]) << 24;
		case 3:
			k1 ^= ((long) data[ tail + 2]) << 16;
		case 2:
			k1 ^= ((long) data[ tail + 1]) << 8;
		case 1:
			k1 ^= ((long) data[ tail]);
			k1 *= c1;
			k1 = rotl64( k1, 31);
			k1 *= c2;
			h1 ^= k1;
		};

		// Finalization

		h1 ^= length;
		h2 ^= length;

		h1 += h2;
		h2 += h1;

		h1 = fmix( h1);
		h2 = fmix( h2);

		h1 += h2;
		h2 += h1;

		return new long[] { h1, h2};
	}

	/**
	 * Generates 128 bit hash from a substring.
	 * 
	 * @param text string to hash
	 * @param from starting index
	 * @param length length of the substring to hash
	 * @param seed seed
	 * @return 128 bit hash of the given array
	 */
	public static long hash128_64( final String text, int from, int length,
			long seed) {
		final int nblocks = length * 2 / 16; // Process as 128-bit blocks.

		long h1 = seed;
		long h2 = seed;

		long c1 = 0x87c37b91114253d5L;
		long c2 = 0x4cf5ad432745937fL;

		// Body

		for (int i = 0; i < nblocks; i++) {
			long k1 = getblock( text, from, i * 2 + 0);
			long k2 = getblock( text, from, i * 2 + 1);

			k1 *= c1;
			k1 = rotl64( k1, 31);
			k1 *= c2;
			h1 ^= k1;

			h1 = rotl64( h1, 27);
			h1 += h2;
			h1 = h1 * 5 + 0x52dce729;

			k2 *= c2;
			k2 = rotl64( k2, 33);
			k2 *= c1;
			h2 ^= k2;

			h2 = rotl64( h2, 31);
			h2 += h1;
			h2 = h2 * 5 + 0x38495ab5;
		}

		// Tail

		final int tail = from + (nblocks * 16 / 2);

		long k1 = 0;
		long k2 = 0;

		switch (length & (15 / 2)) {
		case 7:
			k2 ^= ((long) text.charAt( tail + 6)) << 32;
		case 6:
			k2 ^= ((long) text.charAt( tail + 5)) << 16;
		case 5:
			k2 ^= ((long) text.charAt( tail + 4)) << 0;
			k2 *= c2;
			k2 = rotl64( k2, 33);
			k2 *= c1;
			h2 ^= k2;
		case 4:
			k1 ^= ((long) text.charAt( tail + 3)) << 48;
		case 3:
			k1 ^= ((long) text.charAt( tail + 2)) << 32;
		case 2:
			k1 ^= ((long) text.charAt( tail + 1)) << 16;
		case 1:
			k1 ^= ((long) text.charAt( tail)) << 0;
			k1 *= c1;
			k1 = rotl64( k1, 31);
			k1 *= c2;
			h1 ^= k1;
		};

		// Finalization

		h1 ^= length * 2;
		h2 ^= length * 2;

		h1 += h2;
		h2 += h1;

		h1 = fmix( h1);
		h2 = fmix( h2);

		h1 += h2;
		h2 += h1;

		return h1 ^ h2;
	}
	
	
}
