package org.sqlite.sqlite4.lsm.raw;

import junit.framework.TestCase;
import sun.misc.Unsafe;

import java.io.DataInput;
import java.io.File;

public class DatabaseTest extends TestCase {
    private static final Unsafe unsafe;
    static {
        unsafe = Helper.getUnsafe();
    }
    private static final long environment = Environment.getDefaultEnvironment();

    public void testNewOpenClose() {
        long scratchpad = 0;
        String filename = "testDatabase_NewOpenClose.db";
        int errorCode;
        try {
            scratchpad = Lsm.malloc(environment, 64);
            long databaseReference = scratchpad;
            long filenameAddress = scratchpad + 8;
            errorCode = Database._new(environment, databaseReference);
            assertEquals(Database.OK, errorCode);
            long database = unsafe.getLong(databaseReference);
            Helper.putAsciiStringAsBytes(filenameAddress, filename + "\000");

            errorCode = Database.open(database, filenameAddress);
            assertEquals(Database.OK, errorCode);

            assertEquals(environment, Environment.getEnvironment(database));

            errorCode = Database.close(database);
            assertEquals(Database.OK, errorCode);
        } finally {
            if (scratchpad != 0)
                Lsm.free(environment, scratchpad);
            (new File(filename)).delete();
            (new File(filename + "-log")).delete();
            (new File(filename + "-shm")).delete();
        }
    }

    public void testInsertDelete() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testDatabase_InsertDelete.db")) {
            final long database = databaseHolder.getDatabase();
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(4096)) {
                final long scratchpad = memoryBlockHolder.getAddress();

                final int entriesCount = 16;
                final int keySize = 12;
                StringBuilder sb = new StringBuilder();
                String[] keys = new String[entriesCount];
                long[] keyAddresses = new long[entriesCount];
                long currKeyAddress = scratchpad;
                for (int i = 0; i < entriesCount; ++i) {
                    sb.setLength(0);
                    for (int j = 0; j < keySize; ++j) {
                        sb.append((char) ('A' + i));
                    }
                    keys[i] = sb.toString();
                    keyAddresses[i] = currKeyAddress;
                    Helper.putAsciiStringAsBytes(currKeyAddress, keys[i]);
                    currKeyAddress += keySize;
                }
                long valueAddress = currKeyAddress;
                String value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
                Helper.putAsciiStringAsBytes(valueAddress, value);

                for (int i = 0; i < entriesCount; ++i) {
                    errorCode = Database.insert(database, keyAddresses[i], keys[i].length(), valueAddress, value.length());
                    assertEquals(Database.OK, errorCode);
                }

                errorCode = Database.delete(database, keyAddresses[0], keys[0].length());
                assertEquals(Database.OK, errorCode);

                errorCode = Database.deleteRange(database, keyAddresses[1], keys[1].length(), keyAddresses[4], keys[4].length());
                assertEquals(Database.OK, errorCode);
            }
        }
    }

    public void testBeginCommitRollback() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testDatabase_BeginCommitRollback.db")) {
            final long database = databaseHolder.getDatabase();
            // Open a top-level TX
            errorCode = Database.begin(database, 1);
            assertEquals(Database.OK, errorCode);
            // Open a nested TX
            errorCode = Database.begin(database, 2);
            assertEquals(Database.OK, errorCode);
            // Rollback the nested TX
            errorCode = Database.rollback(database, 1);
            assertEquals(Database.OK, errorCode);
            // Commit the top-level TX
            errorCode = Database.commit(database, 0);
            assertEquals(Database.OK, errorCode);

            // Open a top-level TX
            errorCode = Database.begin(database, 1);
            assertEquals(Database.OK, errorCode);
            // Open a nested TX
            errorCode = Database.begin(database, 2);
            assertEquals(Database.OK, errorCode);
            // Commit the nested TX
            errorCode = Database.commit(database, 1);
            assertEquals(Database.OK, errorCode);
            // Rollback the top-level TX
            errorCode = Database.rollback(database, 0);
            assertEquals(Database.OK, errorCode);
        }
    }

    public void testConfigInteger() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testDatabase_ConfigInteger.db")) {
            long database = databaseHolder.getDatabase();
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(64)) {
                final long scratchpad = memoryBlockHolder.getAddress();

                // Query page size;
                unsafe.putLong(scratchpad, 0);
                errorCode = Database.config(database, Database.CONFIG_PAGE_SIZE, scratchpad);
                assertEquals(Database.OK, errorCode);
                assertEquals(4096, unsafe.getLong(scratchpad));

                // Query block size
                unsafe.putLong(scratchpad, 0);
                errorCode = Database.config(database, Database.CONFIG_BLOCK_SIZE, scratchpad);
                assertEquals(Database.OK, errorCode);
                assertEquals(1024, unsafe.getLong(scratchpad));
            }
        }
    }
}
