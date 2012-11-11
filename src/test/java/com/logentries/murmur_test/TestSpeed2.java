package com.logentries.murmur_test;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class TestSpeed2 {

	/** Number of generated hashes for fixed word test. */
	final static int FIXED_ATTEMPTS = 2000000;
	/** Number of generated hashes for variable word test. */
	final static int VARIABLE_ATTEMPTS = 20000;
	/** Maximal size of a word to test. */
	final static int MAX_WORD_SIZE = 11;
	/** Number of words to test. */
	final static int WORDS = 2000;

	@Test
	public void testSpeedFixed() {
		final String S = "Eithne Ní Bhraonáin";

		System.err.println( com.logentries.murmur.MurmurHashV3.hash128_64( S));
		System.err.println( ie.ucd.murmur.MurmurHashV3.hash128_64( S));

		final String[] strs = { "aaaaa", "ssad", "wertygfdsaa", "256rtygds2",
				"1967", "MeAvE", "xxxxxxxxx-ddd-yyyyy",};

		// Is it the same?
		for (final String str : strs) {
			System.err.println( String.format( "%-20s  %016x  %016x", str,
					com.logentries.murmur.MurmurHashV3.hash128_64( str),
					ie.ucd.murmur.MurmurHashV3.hash128_64( str)));
		}

		/*
		 * com.logentries.murmur
		 */

		long h0 = com.logentries.murmur.MurmurHashV3.hash128_64( S);

		// Warm up
		for (int i = 0; i < FIXED_ATTEMPTS; ++i) {
			for (final String str : strs) {
				com.logentries.murmur.MurmurHashV3.hash128_64( str);
			}
		}

		final long b0 = System.currentTimeMillis();
		for (int i = 0; i < FIXED_ATTEMPTS; ++i) {
			for (final String str : strs) {
				h0 += com.logentries.murmur.MurmurHashV3.hash128_64( str);
			}
		}
		final long e0 = System.currentTimeMillis();

		/*
		 * ie.ucd.murmur
		 */

		long h1 = ie.ucd.murmur.MurmurHashV3.hash128_64( S);

		// Warm up
		for (int i = 0; i < FIXED_ATTEMPTS; ++i) {
			for (final String str : strs) {
				ie.ucd.murmur.MurmurHashV3.hash128_64( str);
			}
		}

		final long b1 = System.currentTimeMillis();
		for (int i = 0; i < FIXED_ATTEMPTS; ++i) {
			for (final String str : strs) {
				h1 += ie.ucd.murmur.MurmurHashV3.hash128_64( str);
			}
		}
		final long e1 = System.currentTimeMillis();

		/*
		 * Final
		 */

		System.err.println();
		System.err.println( "Fixed strings");
		System.err.println( String.format( "JNI: %f %08x", (e0 - b0) / 1000.0,
				h0));
		System.err.println( String.format( "Java:  %f %08x", (e1 - b1) / 1000.0,
				h1));
	}

	long variableWordsUcd( ArrayList<String> words) {
		long h = 0;
		Random random = new Random( 10);

		for (final String word : words) {
			// Calculate word boundary
			int from = random.nextInt( word.length() + 1);
			int to = random.nextInt( word.length() + 1);
			if (from > to) {
				int _ = from;
				from = to;
				to = _;
			}

			for (int attempt = 0; attempt < VARIABLE_ATTEMPTS; attempt++) {
				h += ie.ucd.murmur.MurmurHashV3.hash128_64( word, from, to
						- from, 0x9ee73d188796670eL);
			}
		}
		return h;
	}

	long variableWordsLe( ArrayList<String> words) {
		long h = 0;
		Random random = new Random( 10);

		for (final String word : words) {
			// Calculate word boundary
			int from = random.nextInt( word.length() + 1);
			int to = random.nextInt( word.length() + 1);
			if (from > to) {
				int _ = from;
				from = to;
				to = _;
			}

			for (int attempt = 0; attempt < VARIABLE_ATTEMPTS; attempt++) {
				h += com.logentries.murmur.MurmurHashV3.hash128_64( word, from,
						to - from, 0x9ee73d188796670eL);
			}
		}
		return h;
	}

	@Test
	public void testSpeedVariable() {
		System.err.println();
		System.err.println( "String subset");
		
		// Generate random strings
		ArrayList<String> words = new ArrayList<String>();

		Random random = new Random();
		StringBuilder builder = new StringBuilder();

		for (int j = 0; j < WORDS; j++) {
			// Word size
			int length = random.nextInt( MAX_WORD_SIZE);
			// Generate the string
			builder.setLength( 0);
			for (int i = 0; i < length; i++) {
				char c;
				if (random.nextInt( 8) == 0) {
					// Unicode
					c = (char) random.nextInt( 65000);
				} else {
					// ASCII subset
					c = (char) (32 + random.nextInt( 90));
				}
				builder.append( c);
			}
			words.add( builder.toString());
		}

		/*
		 * com.logentries.murmur
		 */

		// Warm up
		variableWordsUcd( words);
		// Do the test
		final long b0 = System.currentTimeMillis();
		long h0 = variableWordsUcd( words);
		final long e0 = System.currentTimeMillis();

		/*
		 * ie.ucd.murmur
		 */

		// Warm up
		variableWordsLe( words);
		// Do the test
		final long b1 = System.currentTimeMillis();
		long h1 = variableWordsLe( words);
		final long e1 = System.currentTimeMillis();

		/*
		 * Final
		 */

		System.err.println( String.format( "Java: %f %08x", (e0 - b0) / 1000.0,
				h0));
		System.err.println( String.format( "JNI:  %f %08x", (e1 - b1) / 1000.0,
				h1));
	}
}
