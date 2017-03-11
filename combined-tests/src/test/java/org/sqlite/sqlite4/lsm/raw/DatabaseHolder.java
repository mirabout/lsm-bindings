package org.sqlite.sqlite4.lsm.raw;

import java.io.Closeable;
import java.io.File;

class DatabaseHolder implements Closeable {
    private long database = 0;
    private String filename;

    DatabaseHolder(String filename) {
        this.filename = filename;

        long environment = Environment.getDefaultEnvironment();
        long database = 0;
        long scratchpad = 0;
        int errorCode;
        try {
            scratchpad = Lsm.malloc(environment, 4096);
            if (scratchpad == 0)
                throw new RuntimeException("Cannot allocate a native memory scratchpad");
            errorCode = Database._new(environment, scratchpad);
            if (errorCode != Database.OK)
                throw new RuntimeException("Database._new() returned error code " + ErrorCodes.getErrorString(errorCode));
            database = Helper.getUnsafe().getLong(scratchpad);
            Helper.putAsciiStringAsBytes(scratchpad, filename + "\000");
            errorCode = Database.open(database, scratchpad);
            if (errorCode != Database.OK)
                throw new RuntimeException("Database.open() returned error code " + ErrorCodes.getErrorString(errorCode));
            this.database = database;
            database = 0;
        } finally {
            if (database != 0)
                Database.close(database);
            if (scratchpad != 0)
                Lsm.free(environment, scratchpad);
        }
    }

    long getDatabase() {
        assert(database != 0);
        return database;
    }

    @Override
    public void close() {
        assert(database != 0);

        // Close the database before deleting files
        int errorCode = Database.close(database);
        // Delete files anyway
        (new File(filename)).delete();
        (new File(filename + "-log")).delete();
        (new File(filename + "-shm")).delete();

        if (errorCode != Database.OK)
            throw new RuntimeException("Database.close() returned error code " + ErrorCodes.getErrorString(errorCode));

        database = 0;
    }

    void easyInsert(String key, String value) {
        try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(key.length() + value.length())) {
            final long keyAddress = memoryBlockHolder.getAddress();
            final long valueAddress = memoryBlockHolder.getAddress() + key.length();
            Helper.putAsciiStringAsBytes(keyAddress, key);
            Helper.putAsciiStringAsBytes(valueAddress, value);
            int errorCode = Database.insert(database, keyAddress, key.length(), valueAddress, value.length());
            if (errorCode != Database.OK)
                throw new RuntimeException("Database.insert() returned error code " + ErrorCodes.getErrorString(errorCode));
        }
    }
}
