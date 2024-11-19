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

        Assertions.assertEquals(0x0100, emulator.getCpu().getIc());
    }

    @Test
    void testNUL() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x00
        };

        emulator.loadProgram(program);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
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

        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x01FF, emulator.getCpu().getIc());
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

    // R0 != 0x00
    @Test
    void testJZ0_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x19
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x01);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    // R0 == 0x00
    @Test
    void testJZ0_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x19
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x00);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x01FF, emulator.getCpu().getIc());
    }

    // R1 < R2
    @Test
    void testJGW_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x20
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x01);
        emulator.getCpu().setR2(0x02);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    // R1 == R2
    @Test
    void testJGW_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x20
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x01);
        emulator.getCpu().setR2(0x01);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    // R1 > R2
    @Test
    void testJGW_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x20
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x02);
        emulator.getCpu().setR2(0x01);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x01FF, emulator.getCpu().getIc());
    }

    // R1 < R2
    @Test
    void testJEW_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x21
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x01);
        emulator.getCpu().setR2(0x02);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    // R1 > R2
    @Test
    void testJEW_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x21
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x02);
        emulator.getCpu().setR2(0x01);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0101, emulator.getCpu().getIc());
    }

    // R1 == R2
    @Test
    void testJEW_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x21
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x02);
        emulator.getCpu().setR2(0x02);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x01FF, emulator.getCpu().getIc());
    }

    @Test
    void testOR0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x22, 0x11
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x22);

        emulator.run();

        Assertions.assertEquals(0x33, emulator.getCpu().getR0());
    }

    @Test
    void testAN0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x23, 0x09
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x33);

        emulator.run();

        Assertions.assertEquals(0x01, emulator.getCpu().getR0());
    }

    // R0 gleich dem nachfolgenden Byte
    @Test
    void testJE0_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x24, 0xCC
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xCC);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x01FF, emulator.getCpu().getIc());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testJE0_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x24, 0xCC
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xEE);
        emulator.getCpu().setAr(0x01FF);

        emulator.run();

        Assertions.assertEquals(0x0102, emulator.getCpu().getIc());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testC01() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x25
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xFE);

        emulator.run();

        Assertions.assertEquals(0xFE, emulator.getCpu().getR1());
    }

    // R0 ungleich dem nachfolgenden Byte
    @Test
    void testC02() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x26
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xFE);

        emulator.run();

        Assertions.assertEquals(0xFE, emulator.getCpu().getR2());
    }

    // Kein Übelauf von r1, kein Übelauf von r2
    @Test
    void testIRW_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x27
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x00);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0x01, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    // Übelauf von r1, kein Übelauf von r2
    @Test
    void testIRW_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x27
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0xFF);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR1());
        Assertions.assertEquals(0x01, emulator.getCpu().getR2());
    }

    // Übelauf von r1, übelauf von r2
    @Test
    void testIRW_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x27
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0xFF);
        emulator.getCpu().setR2(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR1());
        Assertions.assertEquals(0xFF, emulator.getCpu().getR2());
    }

    // Kein negatives Ergebnis, Kein Unterlauf von r2
    @Test
    void testDRW_1() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x28
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x02);
        emulator.getCpu().setR2(0x02);

        emulator.run();

        Assertions.assertEquals(0x01, emulator.getCpu().getR1());
        Assertions.assertEquals(0x02, emulator.getCpu().getR2());
    }

    // negatives Ergebnis, Kein Unterlauf von r2
    @Test
    void testDRW_2() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x28
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x00);
        emulator.getCpu().setR2(0x02);

        emulator.run();

        Assertions.assertEquals(0x01, emulator.getCpu().getR1());
        Assertions.assertEquals(0x01, emulator.getCpu().getR2());
    }

    // negatives Ergebnis, Unterlauf von r2
    @Test
    void testDRW_3() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x28
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR1(0x00);
        emulator.getCpu().setR2(0x00);

        emulator.run();

        Assertions.assertEquals(0x00, emulator.getCpu().getR1());
        Assertions.assertEquals(0x00, emulator.getCpu().getR2());
    }

    @Test
    void testX03() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x29
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xAA);
        emulator.getCpu().setR3(0xFF);

        emulator.run();

        Assertions.assertEquals(0xFF, emulator.getCpu().getR0());
        Assertions.assertEquals(0xAA, emulator.getCpu().getR3());
    }

    @Test
    void testC03() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x2A
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0xAA);

        emulator.run();

        Assertions.assertEquals(0xAA, emulator.getCpu().getR3());
    }

    @Test
    void testC30() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x2B
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR3(0xAA);

        emulator.run();

        Assertions.assertEquals(0xAA, emulator.getCpu().getR0());
    }

    @Test
    void testPL0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x2C
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x01);

        emulator.run();

        Assertions.assertEquals(0x02, emulator.getCpu().getR0());
    }

    @Test
    void testPR0() {
        Emulator emulator = new Emulator();

        int[] program = {
                0x2D
        };

        emulator.loadProgram(program);

        emulator.getCpu().setR0(0x02);

        emulator.run();

        Assertions.assertEquals(0x01, emulator.getCpu().getR0());
    }

}