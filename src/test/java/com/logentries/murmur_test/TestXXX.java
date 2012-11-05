package com.logentries.re2_test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.lang.StringBuffer;

//import com.logentries.murmur.MurmurHashV3;
//import ie.ucd.murmur.MurmurHashV3;

public class TestXXX {
    static String getXXX(final int len) {
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            sb.append('x');
        }
        return sb.toString();
    }

    @Test
    public void testThreads() {
        final String[] xxx = {"xxx",
                              "xxxxxxxxxxxxxxxxxx",
                              "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
                              getXXX(256*1024),
                              getXXX(256*1024*1024)};

        for (int i = 0; i < xxx.length; ++i) {
            final long hash1 = com.logentries.murmur.MurmurHashV3.hash128_64(xxx[i]);
            final long hash2 = ie.ucd.murmur.MurmurHashV3.hash128_64(xxx[i]);
            System.err.println("i=" + i + "\thash1=" + hash1 + "; hash2=" + hash2);

            assertEquals(hash1, hash2);
        }
    }
}
