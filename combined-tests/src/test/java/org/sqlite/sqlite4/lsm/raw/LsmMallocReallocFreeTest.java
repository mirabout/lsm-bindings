package org.sqlite.sqlite4.lsm.raw;

import junit.framework.TestCase;

import sun.misc.Unsafe;

public class LsmMallocReallocFreeTest extends TestCase {
    // Force Helper class to be initialized and load a native library before Environment is accessed
    private static final Unsafe unsafe;
    static {
        unsafe = Helper.getUnsafe();
    }
    private long environment = Environment.getDefaultEnvironment();

    public void testMallocFree() {
        int block1Size = 16;
        int block2Size = Byte.MAX_VALUE;
        int block3Size = Short.MAX_VALUE;

        long block1 = Lsm.malloc(environment, block1Size);
        long block2 = Lsm.malloc(environment, block2Size);
        long block3 = Lsm.malloc(environment, block3Size);

        assertFalse(0 == block1);
        assertFalse(0 == block2);
        assertFalse(0 == block3);

        // Test array access for all memory block elements
        for (int i = 0; i < block1Size / 4; ++i)
            unsafe.putInt(block1 + i * 4, i);
        for (int i = 0; i < block2Size / 4; ++i)
            unsafe.putInt(block2 + i * 4, i);
        for (int i = 0; i < block3Size / 4; ++i)
            unsafe.putInt(block3 + i * 4, i);

        for (int i = 0; i < block1Size / 4; ++i)
            assertEquals(i, unsafe.getInt(block1 + i * 4));
        for (int i = 0; i < block2Size / 4; ++i)
            assertEquals(i, unsafe.getInt(block2 + i * 4));
        for (int i = 0; i < block3Size / 4; ++i)
            assertEquals(i, unsafe.getInt(block3 + i * 4));

        Lsm.free(environment, block1);
        Lsm.free(environment, block2);
        Lsm.free(environment, block3);
    }

    public void testReallocFree() {
        final int lesserSize = Byte.MAX_VALUE;
        final int greaterSize = Short.MAX_VALUE;
        assert(lesserSize < greaterSize);

        long block1 = Lsm.malloc(environment, lesserSize);
        long block2 = Lsm.malloc(environment, greaterSize);

        assertFalse(0 == block1);
        assertFalse(0 == block2);

        for (int i = 0; i < lesserSize; ++i)
            unsafe.putByte(block1 + i, (byte)(i & Byte.MAX_VALUE));
        for (int i = 0; i < greaterSize; ++i)
            unsafe.putByte(block2 + i, (byte)(i & Byte.MAX_VALUE));

        block1 = Lsm.realloc(environment, block1, greaterSize);
        block2 = Lsm.realloc(environment, block2, lesserSize);

        // Check whether first lesserSize bytes of chunks have been kept
        for (int i = 0; i < lesserSize; ++i) {
            byte expected = (byte)(i & Byte.MAX_VALUE);
            assertEquals(expected, unsafe.getByte(block1 + i));
            assertEquals(expected, unsafe.getByte(block2 + i));
        }

        Lsm.free(environment, block1);
        Lsm.free(environment, block2);
    }
}
