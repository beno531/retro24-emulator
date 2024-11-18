package de.ostfalia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmulatorTest {

    // Loader Test
    @Test
    void testLOADER() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x01,
                0x02,
                0x03,
        };

        emulator.loadProgram(program);

        Assertions.assertEquals(0x01, emulator.getMemory().read(0x0100));
        Assertions.assertEquals(0x02, emulator.getMemory().read(0x0101));
        Assertions.assertEquals(0x03, emulator.getMemory().read(0x0102));
    }

    @Test
    void testHLT() {
        Emulator emulator = new Emulator();

        int[] program = {
                0xFF
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    @Test
    void testNUL() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x00
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x0102, emulator.getCpu().getIc());
    }

    @Test
    void testMAR_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x01, 0x34, 0x12
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x1234, emulator.getCpu().getAr());
    }

    @Test
    void testMAR_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x01, 0x34, 0x12,
                0x01, 0x78, 0x56
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x5678, emulator.getCpu().getAr());
    }

    @Test
    void testSIC() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x02
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0x0200);

        emulator.run();

        Assertions.assertEquals(0x00, emulator.getMemory().read(0x0200));
        Assertions.assertEquals(0x01, emulator.getMemory().read(0x0201));
    }

    @Test
    void testRAR() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x03
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x34);
        emulator.getCpu().setR2(0x12);

        emulator.run();

        Assertions.assertEquals(0x1234, emulator.getCpu().getAr());
    }

    // Kein Überlauf
    @Test
    void testAAR_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                (byte) 0x04
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xFF);
        emulator.getCpu().setAr(0x0900);

        emulator.run();

        Assertions.assertEquals(0x09FF, emulator.getCpu().getAr());
    }

    // Überlauf wird verworfen
    @Test
    void testAAR_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x04
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xFF);
        emulator.getCpu().setAr(0xFFFF);

        emulator.run();

        Assertions.assertEquals(0xFFFF, emulator.getCpu().getAr());
    }

    // Kein Überlauf
    @Test
    void testIR0_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x05
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x34);

        emulator.run();

        Assertions.assertEquals(0x35, emulator.getCpu().getR0());
    }

    // Überlauf
    @Test
    void testIR0_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x05
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR0());
    }

    // Kein Überlauf von r1, Kein Überlauf von r2
    @Test
    void testA01_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x06
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xF0);
        emulator.getCpu().setR1(0x09);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0xF9, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    // Überlauf von r1, Kein Überlauf von r2
    @Test
    void testA01_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x06
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xAA);
        emulator.getCpu().setR1(0xF0);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR1());
        Assertions.assertEquals(0x01, emulator.getCpu().getR2());
    }

    // Überlauf von r1 und r2
    @Test
    void testA01_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x06
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xAA);
        emulator.getCpu().setR1(0xF0);
        emulator.getCpu().setR2(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR1());
        Assertions.assertEquals(0xFF, emulator.getCpu().getR2());
    }

    // Kein Unterlauf
    @Test
    void testDR0_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x07
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x0A);

        emulator.run();

        Assertions.assertEquals(0x09, emulator.getCpu().getR0());
    }

    // Unterlauf
    @Test
    void testDR0_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x07
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x00);

        emulator.run();

        Assertions.assertEquals(0x00, emulator.getCpu().getR0());
    }

    // Kein Unterlauf, kein negatives Ergebnis
    @Test
    void testS01_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x08
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x03);
        emulator.getCpu().setR1(0x7F);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0x03, emulator.getCpu().getR0());
        Assertions.assertEquals(0x7C, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    // Kein Unterlauf, negatives Ergebnis
    @Test
    void testS01_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x08
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x08);
        emulator.getCpu().setR1(0x04);
        emulator.getCpu().setR2(0x06);

        emulator.run();

        Assertions.assertEquals(0x08, emulator.getCpu().getR0());
        Assertions.assertEquals(0x04, emulator.getCpu().getR1());
        Assertions.assertEquals(0x05, emulator.getCpu().getR2());
    }

    // Unterlauf, negatives Ergebnis
    @Test
    void testS01_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x08
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x08);
        emulator.getCpu().setR1(0x04);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0x08, emulator.getCpu().getR0());
        Assertions.assertEquals(0x00, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    @Test
    void testX12() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x09
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0xAA);
        emulator.getCpu().setR2(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR1());
        Assertions.assertEquals(0xAA, emulator.getCpu().getR2());
    }

    @Test
    void testX01() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x10
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xAA);
        emulator.getCpu().setR1(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR0());
        Assertions.assertEquals(0xAA, emulator.getCpu().getR1());
    }

    @Test
    void testJMP() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x11
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0x01FFF);

        emulator.run();

        Assertions.assertEquals(0x2000, emulator.getCpu().getIc());
    }

    @Test
    void testSR0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x12
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0xAAAA);
        emulator.getCpu().setR0(0xBB);

        emulator.run();

        Assertions.assertEquals(0xBB, emulator.getMemory().read(0xAAAA));
    }

    @Test
    void testSRW() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x13
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0xAAAA);
        emulator.getCpu().setR1(0xBB);
        emulator.getCpu().setR2(0xCC);

        emulator.run();

        Assertions.assertEquals(0xBB, emulator.getMemory().read(0xAAAA));
        Assertions.assertEquals(0xCC, emulator.getMemory().read(0xAAAB));
    }

    @Test
    void testLR0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x14
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0xAAAA);
        emulator.getMemory().write(0xAAAA, 0xEE);

        emulator.run();

        Assertions.assertEquals(0xEE, emulator.getCpu().getR0());
    }

    @Test
    void testLRW() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x15
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0xAAAA);
        emulator.getMemory().write(0xAAAA, 0xEE);
        emulator.getMemory().write(0xAAAB, 0xDD);

        emulator.run();

        Assertions.assertEquals(0xEE, emulator.getCpu().getR1());
        Assertions.assertEquals(0xDD, emulator.getCpu().getR2());
    }

    @Test
    void testTAW() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x16
        };

        emulator.loadProgram(program);

        emulator.getCpu().setAr(0x1234);

        emulator.run();

        Assertions.assertEquals(0x34, emulator.getCpu().getR1());
        Assertions.assertEquals(0x12, emulator.getCpu().getR2());
    }

    @Test
    void testMR0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x17, 0x99
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x99, emulator.getCpu().getR0());
    }

    @Test
    void testMRW() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x18, 0x11, 0x22
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x11, emulator.getCpu().getR1());
        Assertions.assertEquals(0x22, emulator.getCpu().getR2());
    }
/*
    // R0 != 0x00
    @Test
    void testJZ0_1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x19,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x01);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD);

        cpu.startup();

        Assertions.assertEquals(0x0101, cpu.getIc());
    }

    // R0 == 0x00
    @Test
    void testJZ0_2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x19
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x00);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD); // TODO

        cpu.startup();

        Assertions.assertEquals(0x00EE, cpu.getIc());
    }
/*
    // R1 < R2
    @Test
    void testJGW_1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x20,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x01);
        cpu.setR2(0x02);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD);

        cpu.startup();

        Assertions.assertEquals(0x0101, cpu.getIc());
    }

    // R1 == R2
    @Test
    void testJGW_2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x20,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x01);
        cpu.setR2(0x01);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD);

        cpu.startup();

        Assertions.assertEquals(0x0101, cpu.getIc());
    }

    // R1 > R2
    @Test
    void testJGW_3() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x20
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x02);
        cpu.setR2(0x01);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD); // TODO

        cpu.startup();

        Assertions.assertEquals(0x00EE, cpu.getIc());
    }

    // R1 < R2
    @Test
    void testJEW_1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x21,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x01);
        cpu.setR2(0x02);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD);

        cpu.startup();

        Assertions.assertEquals(0x0101, cpu.getIc());
    }

    // R1 > R2
    @Test
    void testJEW_2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x21,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x02);
        cpu.setR2(0x01);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD);

        cpu.startup();

        Assertions.assertEquals(0x0101, cpu.getIc());
    }

    // R1 == R2
    @Test
    void testJEW_3() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x21
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR1(0x02);
        cpu.setR2(0x02);
        cpu.setAr(0x8888);
        memory.write(0x8888, 0XEE);
        memory.write(0x00EE, 0XDD); // TODO

        cpu.startup();

        Assertions.assertEquals(0x00EE, cpu.getIc());
    }

    @Test
    void testOR0() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x22, 0x11,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x22);

        cpu.startup();

        Assertions.assertEquals(0x33, cpu.getR0());
    }

    @Test
    void testAN0() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x23, 0x09,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x33);

        cpu.startup();

        Assertions.assertEquals(0x01, cpu.getR0());
    }

    // R0 gleich dem nachfolgenden Byte
    @Test
    void testJE0_1() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x24, 0x11,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x11);
        cpu.setAr(0x0011);
        memory.write(0x0011, 0XEE);
        memory.write(0x00EE, 0XDD); // TODO

        cpu.startup();

        Assertions.assertEquals(0x00EE, cpu.getIc());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testJE0_2() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x24, 0x11,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x12);
        memory.write(0x0011, 0XEE);
        memory.write(0x00EE, 0XDD); // TODO

        cpu.startup();

        Assertions.assertEquals(0x0102, cpu.getIc());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testC01() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x25,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xAA);
        cpu.setR1(0x00);

        cpu.startup();

        Assertions.assertEquals(0xAA, cpu.getR1());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testC02() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x26,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0xAA);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertEquals(0xAA, cpu.getR2());
    }
      */

}