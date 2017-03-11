package org.sqlite.sqlite4.lsm.raw;

import junit.framework.TestCase;

import sun.misc.Unsafe;

import java.util.Random;

public class CursorTest extends TestCase {
    private static final Unsafe unsafe;
    static {
        unsafe = Helper.getUnsafe();
    }
    private final long environment = Environment.getDefaultEnvironment();

    public void testOpenClose() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_OpenClose.db")) {
            final long database = databaseHolder.getDatabase();
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(1024)) {
                final long scratchpad = memoryBlockHolder.getAddress();

                errorCode = Cursor.open(database, scratchpad);
                assertEquals(Cursor.OK, errorCode);

                errorCode = Cursor.close(unsafe.getLong(scratchpad));
                assertEquals(Cursor.OK, errorCode);
            }
        }
    }

    static final class CursorHolder implements AutoCloseable {
        private long cursor = 0;

        long getCursor() { return cursor; }

        CursorHolder(long database) {
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(8)) {
                int errorCode = Cursor.open(database, memoryBlockHolder.getAddress());
                if (errorCode != Cursor.OK)
                    throw new RuntimeException("Cursor.open() returned error code " + ErrorCodes.getErrorString(errorCode));
                this.cursor = unsafe.getLong(memoryBlockHolder.getAddress());
            }
        }

        @Override
        public void close() {
            assert(cursor != 0);
            int errorCode = Cursor.close(cursor);
            if (errorCode != Cursor.OK)
                throw new RuntimeException("Cursor.close() returned error code " + ErrorCodes.getErrorString(errorCode));
            cursor = 0;
        }
    }

    private void putDummyValues(DatabaseHolder databaseHolder) {
        databaseHolder.easyInsert("AAAAA", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("AAFII", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("ABYBY", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("CABBB", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("QADYY", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("QUUUX", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("XACCY", "Lorem ipsum dolor amet");
        databaseHolder.easyInsert("ZZXCY", "Lorem ipsum dolor amet");
    }

    public void testSeek() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_Seek.db")) {
            putDummyValues(databaseHolder);
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(1024)) {
                final long keyAddress = memoryBlockHolder.getAddress();
                try (CursorHolder cursorHolder = new CursorHolder(databaseHolder.getDatabase())) {
                    final long cursor = cursorHolder.getCursor();
                    Helper.putAsciiStringAsBytes(keyAddress, "AAABB");
                    // There is no exactly this key.
                    // According to the LSM docs the cursor should be set at EOF
                    // and Cursor.valid() should return non-zero (TODO really? It looks like the opposite is true)
                    errorCode = Cursor.seek(cursor, keyAddress, 5, Cursor.SEEK_EQ);
                    assertEquals(Cursor.OK, errorCode);
                    // Cursor must be invalid
                    assertTrue(Cursor.valid(cursor) == 0);

                    errorCode = Cursor.seek(cursor, keyAddress, 5, Cursor.SEEK_LE);
                    assertEquals(Cursor.OK, errorCode);
                    // Cursor must be valid
                    assertTrue(Cursor.valid(cursor) != 0);

                    errorCode = Cursor.seek(cursor, keyAddress, 5, Cursor.SEEK_GE);
                    assertEquals(Cursor.OK, errorCode);
                    // Cursor must be valid
                    assertTrue(Cursor.valid(cursor) != 0);
                }
            }
        }
    }

    public void testFirstNextCmp() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_FirstNextCmp.db")) {
            putDummyValues(databaseHolder);
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(512)) {
                final long keyAddress = memoryBlockHolder.getAddress() + 8;
                final long cmpResultAddress = memoryBlockHolder.getAddress();
                try (CursorHolder cursorHolder = new CursorHolder(databaseHolder.getDatabase())) {
                    final long cursor = cursorHolder.getCursor();

                    errorCode = Cursor.first(cursor);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(Cursor.valid(cursor) != 0);

                    Helper.putAsciiStringAsBytes(keyAddress, "AAAAA");
                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) == 0);

                    errorCode = Cursor.next(cursor);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(Cursor.valid(cursor) != 0);

                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) > 0);
                }
            }
        }
    }

    public void testLastPrevCmp() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_LastPrevCmp.db")) {
            putDummyValues(databaseHolder);
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(512)) {
                final long keyAddress = memoryBlockHolder.getAddress() + 8;
                final long cmpResultAddress = memoryBlockHolder.getAddress();
                try (CursorHolder cursorHolder = new CursorHolder(databaseHolder.getDatabase())) {
                    final long cursor = cursorHolder.getCursor();

                    errorCode = Cursor.last(cursor);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(Cursor.valid(cursor) != 0);

                    Helper.putAsciiStringAsBytes(keyAddress, "ZZXCY");
                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertEquals(0, unsafe.getInt(cmpResultAddress));

                    errorCode = Cursor.prev(cursor);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(Cursor.valid(cursor) != 0);

                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) < 0);
                }
            }
        }
    }

    public void testCmp() {
        int errorCode;
        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_Cmp.db")) {
            putDummyValues(databaseHolder);
            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(512)) {
                final long keyAddress = memoryBlockHolder.getAddress() + 8;
                final long cmpResultAddress = memoryBlockHolder.getAddress();
                try (CursorHolder cursorHolder = new CursorHolder(databaseHolder.getDatabase())) {
                    final long cursor = cursorHolder.getCursor();

                    Helper.putAsciiStringAsBytes(keyAddress, "QADYY");
                    errorCode = Cursor.seek(cursor, keyAddress, 5, Cursor.SEEK_EQ);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(Cursor.valid(cursor) != 0);

                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) == 0);

                    Helper.putAsciiStringAsBytes(keyAddress, "QAADD");
                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) > 0);

                    Helper.putAsciiStringAsBytes(keyAddress, "QQQKU");
                    errorCode = Cursor.cmp(cursor, keyAddress, 5, cmpResultAddress);
                    assertEquals(Cursor.OK, errorCode);
                    assertTrue(unsafe.getInt(cmpResultAddress) < 0);
                }
            }
        }
    }

    public void testFetch() {
        final Random random = new Random(17);
        int errorCode;
        final int entriesCount = 16;
        String[] keys = new String[entriesCount];
        String[] values = new String[entriesCount];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entriesCount; ++i) {
            sb.setLength(0);
            for (int j = 0, end = 128 + random.nextInt(32); j < end; ++j)
                sb.append((char)('A' + i));
            String result = sb.toString();
            keys[i] = result;
            values[i] = result + result;
        }

        try (DatabaseHolder databaseHolder = new DatabaseHolder("testCursor_Fetch.db")) {
            final long database = databaseHolder.getDatabase();
            for (int i = 0; i < entriesCount; ++i)
                databaseHolder.easyInsert(keys[i], values[i]);

            try (MemoryBlockHolder memoryBlockHolder = new MemoryBlockHolder(32)) {
                final long keyAddressReference = memoryBlockHolder.getAddress();
                final long keySizeReference = memoryBlockHolder.getAddress() + 8;
                final long valueAddressReference = memoryBlockHolder.getAddress() + 16;
                final long valueSizeReference = memoryBlockHolder.getAddress() + 24;
                try (CursorHolder cursorHolder = new CursorHolder(database)) {
                    final long cursor = cursorHolder.getCursor();

                    assertEquals(Cursor.OK, Cursor.last(cursor));
                    assertTrue(Cursor.valid(cursor) != 0);

                    // Iterate over key-value pairs in descending order
                    int i = keys.length - 1;
                    while (Cursor.valid(cursor) != 0) {
                        assertTrue(i >= 0);
                        String key = keys[i];
                        String value = values[i];
                        assert(key != null);
                        assert(value != null);

                        errorCode = Cursor.key(cursor, keyAddressReference, keySizeReference);
                        assertEquals(Cursor.OK, errorCode);
                        long keyAddress = unsafe.getLong(keyAddressReference);
                        int keySize = unsafe.getInt(keySizeReference);
                        String fetchedKey = Helper.getBytesAsAsciiString(keyAddress, keySize);
                        assertEquals(key, fetchedKey);

                        errorCode = Cursor.value(cursor, valueAddressReference, valueSizeReference);
                        assertEquals(Cursor.OK, errorCode);
                        long valueAddress = unsafe.getLong(valueAddressReference);
                        int valueSize = unsafe.getInt(valueSizeReference);
                        String fetchedValue = Helper.getBytesAsAsciiString(valueAddress, valueSize);
                        assertEquals(value, fetchedValue);

                        i--;
                        assertEquals(Cursor.OK, Cursor.prev(cursor));
                    }
                    assert(i == -1);
                }
            }
        }
    }
}
