package org.sqlite.sqlite4.lsm.raw;

class MemoryBlockHolder implements AutoCloseable {
    private long address = 0;
    private long environment;

    long getAddress() { return address; }

    MemoryBlockHolder(int size, long environment) {
        this.environment = environment;
        this.address = Lsm.malloc(environment, size);
        if (this.address == 0)
            throw new RuntimeException("Lsm.malloc(): Cannot allocate a memory block of " + size + "bytes");
    }

    MemoryBlockHolder(int size) {
        this(size, Environment.getDefaultEnvironment());
    }

    @Override
    public void close() {
        if (address != 0) {
            Lsm.free(environment, address);
            address = 0;
        }
    }
}
