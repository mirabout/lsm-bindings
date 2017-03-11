package org.sqlite.sqlite4.lsm.raw;

public class Database extends ErrorCodes {
    private Database() {}

    public static native int _new(long environment, long databaseReference);
    public static native int open(long database, long filenameAddress);
    public static native int close(long database);

    public static final int SAFETY_OFF = 0;
    public static final int SAFETY_NORMAL = 1;
    public static final int SAFETY_FULL = 2;

    public static final int CONFIG_AUTOFLUSH = 1;
    public static final int CONFIG_PAGE_SIZE = 2;
    public static final int CONFIG_SAFETY = 3;
    public static final int CONFIG_BLOCK_SIZE = 4;
    public static final int CONFIG_AUTOWORK = 5;
    public static final int CONFIG_MMAP = 7;
    public static final int CONFIG_USE_LOG = 8;
    public static final int CONFIG_AUTOMERGE = 9;
    public static final int CONFIG_MAX_FREELIST = 10;
    public static final int CONFIG_MULTIPLE_PROCESSES = 11;
    public static final int CONFIG_AUTOCHECKPOINT = 12;

    public static native int config(long database, int paramId, long paramReference);

    public static native int begin(long database, int level);
    public static native int commit(long database, int level);
    public static native int rollback(long database, int level);

    public static native int insert(long database, long keyAddress, int keySize, long valueAddress, int valueSize);
    public static native int delete(long database, long keyAddress, int keySize);
    public static native int deleteRange(long database, long key1Address, int key1Size, long key2Address, int key2Size);
}
