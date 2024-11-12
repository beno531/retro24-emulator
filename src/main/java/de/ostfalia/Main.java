package de.ostfalia;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0xA9, 0x01, // LDA #$01
                (byte) 0xAA,       // TAX
                (byte) 0xE8,       // INX
                (byte) 0x00        // BRK (Abbruch)
        };

        emulator.loadProgram(program, 0x600);
        emulator.run();
    }
}
