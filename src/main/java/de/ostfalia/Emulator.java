package de.ostfalia;

import java.lang.reflect.Constructor;

public class Emulator {

    private Memory memory;
    private CPU cpu;

    public Emulator(){
        this.memory = Memory.getInstance();
        this.cpu = new CPU();
    }

    // Methode zum Laden des Programms in den Speicher
    public void loadProgram(byte[] program, int startAddress) {

        int counter = startAddress;

        for (byte val : program) {
            memory.write(counter, val);
            counter++;
        }

        this.cpu.setIc(startAddress);
    }

    // Haupt-Emulationsschleife
    public void run() {
        cpu.executeInstruction();
    }
}
