package org.sqlite.sqlite4.lsm.raw;

public class Lsm extends ErrorCodes {
    private Lsm() {}

    public static native long malloc(long environment, long size);
    public static native long realloc(long environment, long memoryAddress, long size);
    public static native void free(long environment, long memoryAddress);
}
