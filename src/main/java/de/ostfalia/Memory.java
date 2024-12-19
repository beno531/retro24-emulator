package de.ostfalia;

import java.util.Arrays;

public class Memory {
    private static final int SIZE_IO = 0x0100; // 256 Bytes für I/O-Page (0x0000 - 0x00FF)
    private static final int SIZE_RAM = 0xDC00; // 56320 Bytes für RAM (0x0100 - 0xDFFF)
    private static final int SIZE_VIDEO = 0x2000; // 8192 Bytes für Video (0xE000 - 0xFFFF)

    //private static Memory instance;
    private int[] io;
    private int[] ram;
    private int[] video;

    /*
        Speicher-Layout:
        - I/O-Page bei $0000-$00FF
        - Programmspeicher bei $0100-$DFFF
        - Grafikspeicher bei $E000-$FFFF
     */

    public Memory() {
        io = new int[SIZE_IO];
        ram = new int[SIZE_RAM];
        video = new int[SIZE_VIDEO];

        Arrays.fill(ram, 0xFF); // Setzt den RAM-Bereich von $0100-$DFFF auf 0xFF
        Arrays.fill(video, 0xAA); // Setzt den Video-Bereich von $E000-$FFFF auf 0x00
    }

    /*public static Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
        }
        return instance;
    }*/

    public int read(int address) {
        if (address >= 0x0000 && address <= 0x00FF) {
            return io[address]; // I/O-Bereich
        } else if (address >= 0x0100 && address <= 0xDFFF) {
            return ram[address - 0x0100]; // RAM-Bereich
        } else if (address >= 0xE000 && address <= 0xFFFF) {
            return video[address - 0xE000]; // Video-Bereich
        } else {
            throw new IllegalArgumentException("Ungültige Adresse: " + Integer.toHexString(address));
        }
    }

    public void write(int address, int value) {
        if (address >= 0x0000 && address <= 0x00FF) {
            io[address] = value; // I/O-Bereich
        } else if (address >= 0x0100 && address <= 0xDFFF) {
            ram[address - 0x0100] = value; // RAM-Bereich
        } else if (address >= 0xE000 && address <= 0xFFFF) {
            video[address - 0xE000] = value; // Video-Bereich
        } else {
            throw new IllegalArgumentException("Ungültige Adresse: " + Integer.toHexString(address));
        }
    }
}