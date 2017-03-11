package org.sqlite.sqlite4.lsm.raw;

public class ErrorCodes {
    ErrorCodes() {}

    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int BUSY = 2;
    public static final int NOMEM = 7;
    public static final int IOERR = 10;
    public static final int CORRUPT = 11;
    public static final int FULL = 13;
    public static final int CANTOPEN = 14;
    public static final int PROTOCOL = 15;
    public static final int MISUSE = 21;

    public static String getErrorString(int code) {
        switch (code) {
            case OK: return "OK";
            case ERROR: return "ERROR";
            case BUSY: return "BUSY";
            case NOMEM: return "NOMEM";
            case IOERR: return "IOERR";
            case CORRUPT: return "CORRUPT";
            case CANTOPEN: return "CANTOPEN";
            case PROTOCOL: return "PROTOCOL";
            case MISUSE: return "MISUSE";
            default: return "(UNKNOWN ERROR)";
        }
    }
}
