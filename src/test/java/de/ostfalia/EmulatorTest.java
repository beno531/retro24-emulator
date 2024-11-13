package de.ostfalia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmulatorTest {


    @Test
    void testMAR1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0xDD
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.startup();

        Assertions.assertEquals(0x1234, cpu.getAr());
    }
    @Test
    void testMAR2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0x01, 0x78, 0x56,
                (byte) 0xDD
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.startup();

        Assertions.assertEquals(0x5678, cpu.getAr());

    }
}