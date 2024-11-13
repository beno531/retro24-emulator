package de.ostfalia;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0x01, 0x78, 0x56,
                (byte) 0xDD
        };

        emulator.loadProgram(program, 0x0004);
        emulator.run();
    }
}
