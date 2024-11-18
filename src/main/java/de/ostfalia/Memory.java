package de.ostfalia;

import java.util.Arrays;

public class Memory {
    private static final int SIZE = 65536; // 64KB Speichergröße
    private static Memory instance;
    private int[] ram;

    /*
        Speicher-Layout: I/O-Page bei $0000-$00FF,
        Programmspeicher: $0100-$DFFF,
        Grafikspeicher: $E000-$FFFF
     */

    private Memory() {
        ram = new int[SIZE];
        Arrays.fill(ram, 0x0100, 0xE000, 0xFF); // $0100-$DFFF set $FF
    }

    public static Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
        }
        return instance;
    }

    public int read(int address) {
        return ram[address];
    }

    public void write(int address, int value) {
        ram[address] = value;
    }

    public void reset() {
        ram = new int[ram.length];
    }
}
