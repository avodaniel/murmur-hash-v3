package com.logentries.murmur_test;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;



public class TestSpeed {
    @Test
    public void xxx() {
        long hash = com.logentries.murmur.MurmurHashV3.hash128_64("Eithne Ní Bhraonáin");
        final int cycles_count = 10000;
        final String[] strs = {"aaaaa", "ssad", "wertygfdsaa", "256rtygds2", "1967", "MeAvE", "xxxxxxxxx-ddd-yyyyy", };

        final long b1 = System.currentTimeMillis();
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash += com.logentries.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        final long e1 = System.currentTimeMillis();
        /* *** *** */
        final long b2 = System.currentTimeMillis();
        for (int i = 0; i < cycles_count; ++i) {
            for (final String str: strs) {
                hash += ie.ucd.murmur.MurmurHashV3.hash128_64(str);
            }
        }
        final long e2 = System.currentTimeMillis();

        System.err.println(String.format("1. %d\n2. %d\n%d", e1 - b1, e2 - b2, hash));
    }
}
