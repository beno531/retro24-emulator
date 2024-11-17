package de.ostfalia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmulatorTest {

    // Loader Test
    @Test
    void testLOADER() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x02,
                (byte) 0xDD
        };

        emulator.loadProgram(program, 0x0100);


        Assertions.assertEquals(0x02, emulator.getMemory().read(0x0100));
        Assertions.assertEquals(0xDD, emulator.getMemory().read(0x0101));
    }

    @Test
    void testHLT() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);


        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    @Test
    void testNUL() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setIc(0x0000);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    @Test
    void testMAR_1() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.run();

        Assertions.assertEquals(0x1234, emulator.getCpu().getAr());
    }

    @Test
    void testMAR_2() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x01, 0x34, 0x12,
                (byte) 0x01, 0x78, 0x56,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.run();

        Assertions.assertEquals(0x5678, emulator.getCpu().getAr());
    }

    @Test
    void testSIC() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x02,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x01234);

        emulator.getCpu().setAr(0x0100);

        emulator.run();

        Assertions.assertEquals(0x34, emulator.getMemory().read(0x0100));
        Assertions.assertEquals(0x12, emulator.getMemory().read(0x0101));
    }

    @Test
    void testRAR() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x03,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR1(0x34);
        emulator.getCpu().setR2(0x12);

        emulator.run();

        Assertions.assertEquals(0x1234, emulator.getCpu().getAr());
    }

    // Kein Überlauf
    @Test
    void testAAR_1() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x04,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0xFF);
        emulator.getCpu().setAr(0x0034);

        emulator.run();

        Assertions.assertEquals(0x0133, emulator.getCpu().getAr());
    }

    // Überlauf wird verworfen
    @Test
    void testAAR_2() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x04,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0xFF);
        emulator.getCpu().setAr(0xFFFF);

        emulator.run();

        Assertions.assertEquals(0xFFFF, emulator.getCpu().getAr());
    }

    // Kein Überlauf
    @Test
    void testIR0_1() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x05,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0x34);

        emulator.run();

        Assertions.assertEquals(0x35, emulator.getCpu().getR0());
    }

    // Überlauf
    @Test
    void testIR0_2() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x05,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR0());
    }

    // Kein Überlauf
    @Test
    void testA01_1() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x06,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0xF0);
        emulator.getCpu().setR1(0x09);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0xF9, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    // Überlauf von r1
    @Test
    void testA01_2() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x06,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

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

        byte[] program = {
                (byte) 0x06,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

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

        byte[] program = {
                (byte) 0x07,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0x0A);

        emulator.run();

        Assertions.assertEquals(0x09, emulator.getCpu().getR0());
    }

    // Unterlauf
    @Test
    void testDR0_2() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x07,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0x00);

        emulator.run();

        Assertions.assertEquals(0x00, emulator.getCpu().getR0());
    }

    // Kein Unterlauf, kein negatives Ergebnis
    @Test
    void testS01_1() {
        Emulator emulator = new Emulator();

        byte[] program = {
                (byte) 0x08,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

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

        byte[] program = {
                (byte) 0x08,
                (byte) 0xFF
        };

        emulator.loadProgram(program, 0x0100);

        emulator.getCpu().setR0(0x02);
        emulator.getCpu().setR1(0xF9);
        emulator.getCpu().setR2(0x05);

        emulator.run();

        Assertions.assertEquals(0x02, emulator.getCpu().getR0());
        Assertions.assertEquals(0x07, emulator.getCpu().getR1());
        Assertions.assertEquals(0x04, emulator.getCpu().getR2());
    }

    // Unterlauf, negatives Ergebnis
    @Test
    void testS01_3() {

        int startAddress = 0x0300;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x08,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setR0(0x02);
        cpu.setR1(0x96);
        cpu.setR2(0x00);

        cpu.startup();

        Assertions.assertTrue(0x02 == cpu.getR0() && 0x00 == cpu.getR1() && 0x00 == cpu.getR2());
    }

    /*

    @Test
    void testX12() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x09,
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
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
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.setAr(0x1234);

        cpu.startup();

        Assertions.assertTrue(0x34 == cpu.getR1() && 0x12 == cpu.getR2());
    }

    @Test
    void testMR0() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x17, (byte) 0x99,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.startup();

        Assertions.assertEquals(0x99, cpu.getR0());
    }

    @Test
    void testMRW() {

        int startAddress = 0x0100;

        CPU cpu = new CPU();
        Loader loader = new Loader();

        byte[] program = {
                (byte) 0x18, (byte) 0x11, (byte) 0x22,
                (byte) 0xFF
        };

        loader.writeProgram(program, startAddress);
        cpu.setIc(startAddress);

        cpu.startup();

        Assertions.assertTrue(0x11 == cpu.getR1() && 0x22 == cpu.getR2());
    }

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