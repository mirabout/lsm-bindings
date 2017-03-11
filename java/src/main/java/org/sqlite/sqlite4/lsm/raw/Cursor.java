package org.sqlite.sqlite4.lsm.raw;

public class Cursor extends ErrorCodes {
    private Cursor() {}

    public static final int SEEK_LEFAST = -2;
    public static final int SEEK_LE = -1;
    public static final int SEEK_EQ = 0;
    public static final int SEEK_GE = 1;

    public static native int open(long database, long cursorPointer);
    public static native int close(long cursor);

    public static native int seek(long cursor, long keyAddress, int keySize, int seekMode);
    public static native int first(long cursor);
    public static native int last(long cursor);
    public static native int next(long cursor);
    public static native int prev(long cursor);
    public static native int valid(long cursor);
    public static native int key(long cursor, long keyAddressPointer, long keySizePointer);
    public static native int value(long cursor, long valueAddressPointer, long valueSizePointer);
    public static native int cmp(long cursor, long keyAddress, int keySize, long resultPointer);
}
