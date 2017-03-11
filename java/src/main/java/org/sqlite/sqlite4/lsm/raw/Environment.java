package org.sqlite.sqlite4.lsm.raw;

public class Environment extends ErrorCodes {
    public Environment() {}

    public static native long getEnvironment(long database);
    public static native long getDefaultEnvironment();
}
