package com.logentries.murmur_test;

import org.junit.Test;



public class TestSpeed2 {
    @Test
    public void speedTest() {
    	final String S = "Eithne Ní Bhraonáin";
    	
    	System.err.println( com.logentries.murmur.MurmurHashV3.hash128_64( S));
    	System.err.println( ie.ucd.murmur.MurmurHashV3.hash128_64( S));
    	
        final int cycles_count = 2000000;
        final String[] strs = {"aaaaa", "ssad", "wertygfdsaa", "256rtygds2", "1967", "MeAvE", "xxxxxxxxx-ddd-yyyyy", };

        // Is it the same?
        for (final String str : strs) {
        	System.err.println( String.format( "%-20s  %016x  %016x",
        			str,
        			com.logentries.murmur.MurmurHashV3.hash128_64( str),
        			ie.ucd.murmur.MurmurHashV3.hash128_64( str)));
        }
        

        /*
         * com.logentries.murmur
         */
        
        long hash1 = com.logentries.murmur.MurmurHashV3.hash128_64( S);
        
        // Warm up
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash1 += com.logentries.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        
        final long b1 = System.currentTimeMillis();
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash1 += com.logentries.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        final long e1 = System.currentTimeMillis();
   
        
        /*
         * ie.ucd.murmur
         */
   
        long hash2 = ie.ucd.murmur.MurmurHashV3.hash128_64( S);
        
        // Warm up
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash2 += ie.ucd.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        
        final long b2 = System.currentTimeMillis();
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash2 += ie.ucd.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        final long e2 = System.currentTimeMillis();

        /*
         * Final
         */
        
        System.err.println(String.format("Java: %f %08x", (e1 - b1)/1000.0, hash1));
        System.err.println(String.format("JNI:  %f %08x", (e2 - b2)/1000.0, hash2));
    }
}
