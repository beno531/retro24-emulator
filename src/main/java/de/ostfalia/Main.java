package de.ostfalia;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x02,
                (byte) 0xDD // TODO
        };

        emulator.loadProgram(program, 0x0004);
        emulator.run();
    }
}
