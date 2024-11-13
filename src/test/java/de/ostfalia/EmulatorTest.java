package de.ostfalia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmulatorTest {
    Memory memory;
    public EmulatorTest(){
        memory = Memory.getInstance();
    }


    @Test
    void testMAR1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0xDD // TODO
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
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.startup();

        Assertions.assertEquals(0x5678, cpu.getAr());
    }

    @Test
    void testSIC() {

        int startAddress = 0x1234;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x02,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);
        cpu.setAr(0x0100);

        cpu.startup();

        Assertions.assertTrue(0x34 == memory.read(0x0100) && 0x12 == memory.read(0x0101));
    }

    @Test
    void testRAR() {

        int startAddress = 0x0200;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x03,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x34);
        cpu.setR2(0x12);

        cpu.startup();

        Assertions.assertEquals(0x1234, cpu.getAr());
    }

    // Kein Überlauf
    @Test
    void testAAR1() {

        int startAddress = 0x0200;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x04,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x34);
        cpu.setAr(0x0034);

        cpu.startup();

        Assertions.assertEquals(0x0068, cpu.getAr());
    }

    // Überlauf wird verworfen
    @Test
    void testAAR2() {

        int startAddress = 0x0200;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x04,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xFF);
        cpu.setAr(0xFFF0);

        cpu.startup();

        Assertions.assertEquals(0xFFFF, cpu.getAr());
    }

    // Kein Überlauf
    @Test
    void testIR01() {

        int startAddress = 0x0200;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x05,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x34);

        cpu.startup();

        Assertions.assertEquals(0x35, cpu.getR0());
    }
}