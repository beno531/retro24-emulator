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
    void testMAR_1() {

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
    void testMAR_2() {

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
    void testAAR_1() {

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
    void testAAR_2() {

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
    void testIR0() {

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

    // Kein Überlauf
    @Test
    void testA01_1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x06,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x04);
        cpu.setR1(0x01);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertTrue(0x05 == cpu.getR1() && 0x00 == cpu.getR2());
    }

    // Überlauf von r1
    @Test
    void testA01_2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x06,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xAA);
        cpu.setR1(0xF0);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertTrue(0xFF == cpu.getR1() && 0x01 == cpu.getR2());
    }

    // Überlauf von r1 und r2
    @Test
    void testA01_3() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x06,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xAA);
        cpu.setR1(0xF0);
        cpu.setR2(0xFF);

        cpu.startup();

        Assertions.assertTrue(0xFF == cpu.getR1() && 0xFF == cpu.getR2());
    }

    // Kein Unterlauf
    @Test
    void testDR0_1() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x07,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x0A);

        cpu.startup();

        Assertions.assertEquals(0x09, cpu.getR0());
    }

    // Unterlauf
    @Test
    void testDR0_2() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x07,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x00);

        cpu.startup();

        Assertions.assertEquals(0x00, cpu.getR0());
    }

    // Kein Unterlauf, keine negatives Ergebnis
    @Test
    void testS01_1() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x08,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x02);
        cpu.setR1(0x06);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertTrue(0x02 == cpu.getR0() && 0x04 == cpu.getR1() && 0x00 == cpu.getR2());
    }

    // Kein Unterlauf, negatives Ergebnis
    @Test
    void testS01_2() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x08,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x02);
        cpu.setR1(0x96);
        cpu.setR2(0x05);

        cpu.startup();

        Assertions.assertTrue(0x02 == cpu.getR0() && 0x6C == cpu.getR1() && 0x04 == cpu.getR2());
    }

    // Unterlauf, negatives Ergebnis
    @Test
    void testS01_3() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x08,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x02);
        cpu.setR1(0x96);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertTrue(0x02 == cpu.getR0() && 0x00 == cpu.getR1() && 0x00 == cpu.getR2());
    }

    @Test
    void testX12() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x09,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0xAA);
        cpu.setR2(0xFF);

        cpu.startup();

        Assertions.assertTrue(0xFF == cpu.getR1() && 0xAA == cpu.getR2());
    }

    @Test
    void testX01() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x10,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xAA);
        cpu.setR1(0xFF);

        cpu.startup();

        Assertions.assertTrue(0xFF == cpu.getR0() && 0xAA == cpu.getR1());
    }

    @Test
    void testJMP() {

        int startAddress = 0x0006;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x11,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0x0003);
        memory.write(0x0003, 0x07);

        cpu.startup();

        Assertions.assertEquals(0x00, cpu.getR0());
    }

    @Test
    void testSR0() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x12,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0xAAAA);
        cpu.setR0(0xBB);

        cpu.startup();

        Assertions.assertEquals(0xBB, memory.read(0xAAAA));
    }

    @Test
    void testSRW() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x13,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0xAAAA);
        cpu.setR1(0xBB);
        cpu.setR2(0xCC);

        cpu.startup();

        Assertions.assertTrue(0xBB == memory.read(0xAAAA) && 0xCC == memory.read(0xAAAB));
    }

    @Test
    void testLR0() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x14,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0xAAAA);
        memory.write(0xAAAA, 0xEE);

        cpu.startup();

        Assertions.assertEquals(0xEE, cpu.getR0());
    }

    @Test
    void testLRW() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x15,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0xAAAA);
        memory.write(0xAAAA, 0xEE);
        memory.write(0xAAAB, 0xFF);

        cpu.startup();

        Assertions.assertTrue(0xEE == cpu.getR1() && 0xFF == cpu.getR2());
    }

    @Test
    void testTAW() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x16,
                (byte) 0xDD // TODO
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0x1234);

        cpu.startup();

        Assertions.assertTrue(0x34 == cpu.getR1() && 0x12 == cpu.getR2());
    }
}