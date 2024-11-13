package de.ostfalia;

public class Memory {
    private static final int SIZE = 65536; // 64KB Speichergröße
    private static Memory instance;
    private byte[] ram;

    private Memory() {
        ram = new byte[SIZE];
    }

    public static Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
        }
        return instance;
    }

    public int read(int address) {
        return ram[address] & 0xFFFF;
    }

    public void write(int address, int value) {
        ram[address] = (byte) (value & 0xFFFF);
    }

    public void reset() {
        ram = new byte[ram.length];
    }
}
