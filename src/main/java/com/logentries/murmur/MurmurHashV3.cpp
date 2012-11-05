#include "MurmurHashV3.h"

#include <stdint.h>

#if 0
static int getblock32( byte[] data, int offset, int index) {
int i4 = offset + index * 4;
return (data[ i4 + 0] & 0xff) + ((data[ i4 + 1] & 0xff) << 8)
+ ((data[ i4 + 2] & 0xff) << 16)
+ ((data[ i4 + 3] & 0xff) << 24);
}
#endif

static jlong fmix(const jlong h) {
    uint64_t uh = static_cast<uint64_t>(h);
    uh ^= uh >> 33;
    uh *= 0xff51afd7ed558ccdL;
    uh ^= uh >> 33;
    uh *= 0xc4ceb9fe1a85ec53L;
    uh ^= uh >> 33;
    return static_cast<jlong>(uh);
}

static jlong getblock(const jbyte *de, const jint offset, const jint index) {
    const jint i8 = offset + index/8;

    return ( (static_cast<jlong>(de[i8 + 0]) & 0xff) )
         + ( (static_cast<jlong>(de[i8 + 1]) & 0xff) << 8 )
         + ( (static_cast<jlong>(de[i8 + 2]) & 0xff) << 16 )
         + ( (static_cast<jlong>(de[i8 + 3]) & 0xff) << 24 )
         + ( (static_cast<jlong>(de[i8 + 4]) & 0xff) << 32 )
         + ( (static_cast<jlong>(de[i8 + 5]) & 0xff) << 40 )
         + ( (static_cast<jlong>(de[i8 + 6]) & 0xff) << 48 )
         + ( (static_cast<jlong>(de[i8 + 7]) & 0xff) << 56 );
}

static long getblock(const jchar *td, const jint offset, const jint index) {
    const jint i8 = offset + index/8/2;
    return ( (static_cast<jlong>(td[i8 + 0]) & 0xffff) )
         + ( (static_cast<jlong>(td[i8 + 1]) & 0xffff) << 16 )
         + ( (static_cast<jlong>(td[i8 + 2]) & 0xffff) << 32 )
         + ( (static_cast<jlong>(td[i8 + 3]) & 0xffff) << 48 );
}


static jint rotl32(const jint val, const jint s) {
    const uint32_t uval = static_cast<uint32_t>(val);
    return static_cast<jint>((uval << s) | (uval >> (32 - s)));
}

static jlong rotl64(const jlong val, const jint s) {
    const uint64_t uval = static_cast<uint64_t>(val);
    return static_cast<jlong>((uval << s) | (uval >> (64 - s)));
}



JNIEXPORT jlongArray JNICALL Java_com_logentries_murmur_MurmurHashV3_fastHash128
  (JNIEnv *env, jclass cls, jlongArray out, jbyteArray data, jint offset, jint length, jlong seed) {
    jbyte *data_elems = env->GetByteArrayElements(data, NULL);
    const jint nblocks = length/16; // Process as 128-bit blocks.

    jlong h1 = seed;
    jlong h2 = seed;

    const jlong c1 = 0x87c37b91114253d5L;
    const jlong c2 = 0x4cf5ad432745937fL;

    // Body

    for (int i = 0; i < nblocks; i++) {
        jlong k1 = getblock(data_elems, offset, i*2 + 0);
        jlong k2 = getblock(data_elems, offset, i*2 + 1);

        k1 *= c1;
        k1 = rotl64(k1, 31);
        k1 *= c2;
        h1 ^= k1;

        h1 = rotl64(h1, 27);
        h1 += h2;
        h1 = h1*5 + 0x52dce729;

        k2 *= c2;
        k2 = rotl64(k2, 33);
        k2 *= c1;
        h2 ^= k2;

        h2 = rotl64(h2, 31);
        h2 += h1;
        h2 = h2*5 + 0x38495ab5;
    }

    // Tail
    jint tail = offset + nblocks*16;
    jlong k1 = 0;
    jlong k2 = 0;

    switch (length & 15) {
    case 15:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 14]) ) << 48;
    case 14:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 13]) ) << 40;
    case 13:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 12]) ) << 32;
    case 12:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 11]) ) << 24;
    case 11:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 10]) ) << 16;
    case 10:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 9]) ) << 8;
    case 9:
        k2 ^= ( static_cast<jlong>(data_elems[tail + 8]) ) << 0;
        k2 *= c2;
        k2 = rotl64(k2, 33);
        k2 *= c1;
        h2 ^= k2;

    case 8:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 7]) ) << 56;
    case 7:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 6]) ) << 48;
    case 6:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 5]) ) << 40;
    case 5:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 4]) ) << 32;
    case 4:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 3]) ) << 24;
    case 3:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 2]) ) << 16;
    case 2:
        k1 ^= ( static_cast<jlong>(data_elems[tail + 1]) ) << 8;
    case 1:
        k1 ^= ( static_cast<jlong>(data_elems[tail]) );
        k1 *= c1;
        k1 = rotl64(k1, 31);
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

    env->ReleaseByteArrayElements(data, data_elems, JNI_ABORT);

    const jlong out_buf[] = { h1, h2, };
    env->SetLongArrayRegion(out, 0, 2, out_buf);
    return out;
}

JNIEXPORT jlong JNICALL Java_com_logentries_murmur_MurmurHashV3_fastHash128_164
  (JNIEnv *env, jclass cls, jstring text, jint from, jint length, jlong seed) {

    const jchar *text_chars = env->GetStringChars(text, NULL);

    const jint nblocks = length*2/16; // Process as 128-bit blocks. //FIXME: why not .../8 ?

    jlong h1 = seed;
    jlong h2 = seed;

    const jlong c1 = 0x87c37b91114253d5L;
    const jlong c2 = 0x4cf5ad432745937fL;

    // Body

    for (jint i = 0; i < nblocks; i++) {
        jlong k1 = getblock(text_chars, from, i*2 + 0);
        jlong k2 = getblock(text_chars, from, i*2 + 1);

        k1 *= c1;
        k1 = rotl64(k1, 31);
        k1 *= c2;
        h1 ^= k1;

        h1 = rotl64(h1, 27);
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

    const jint tail = from + (nblocks*16/2); //why not ...*8 ?

    jlong k1 = 0;
    jlong k2 = 0;

    switch (length & (15 / 2)) {
    case 7:
        k2 ^= (static_cast<jlong>( text_chars[tail + 6] )) << 32;
    case 6:
        k2 ^= (static_cast<jlong>( text_chars[tail + 5] )) << 16;
    case 5:
    case 4:
        k1 ^= (static_cast<jlong>( text_chars[tail + 3] )) << 48;
    case 3:
        k1 ^= (static_cast<jlong>( text_chars[tail + 2] )) << 32;
    case 2:
        k1 ^= (static_cast<jlong>(text_chars[tail + 1] )) << 16;
    case 1:
        k1 ^= (static_cast<jlong>( text_chars[tail] )) << 0;
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

    h1 = fmix(h1);
    h2 = fmix(h2);

    h1 += h2;
    h2 += h1;

    env->ReleaseStringChars(text, text_chars);

    return h1 ^ h2;
}
