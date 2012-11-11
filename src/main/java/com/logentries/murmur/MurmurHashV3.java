package com.logentries.murmur;

public final class MurmurHashV3 {
    static {
        if (!EmbeddedLibraryTools.LOADED_MURMUR) {
            System.loadLibrary("murmur-hash-v3-java");
        }
    }

    private static native long[] fastHash128(long[] out, byte[] data, int offset, int length, long seed);

    public static long[] hash128(byte[] data, int offset, int length, long seed) {
        long[] out = new long[2];
        return fastHash128(out, data, offset, length, seed);
    }

    /**
      * Generates 128 bit hash from byte array with default seed value.
      * 
      * @param data byte array to hash
      * @param length length of the array to hash
      * @return 128 bit hash of the given string
      */
    public static long[] hash128(final byte[] data, int length) {
        return hash128(data, 0, length, 0x9ee73d188796670eL);
    }

    public static native long hash128_64(final String text, int from, int length, long seed);

      /**
       * Generates 128 bit hash from a string.
       * 
       * @param text string to hash
       * @return 128 bit hash of the given string
       */
    public static native long hash128_64(final String text);

    /**
     * Generates 128 bit hash from a string.
     * 
     * @param text string to hash
     * @param salt initial salt
     * @return 128 bit hash of the given string
     */
    public static native long hash128_64(final String text, long salt);
}
