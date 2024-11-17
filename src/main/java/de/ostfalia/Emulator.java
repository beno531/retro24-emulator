package de.ostfalia;

import java.lang.reflect.Constructor;

public class Emulator {

    private Memory memory;
    private CPU cpu;
    private Loader loader;

    public Emulator(){
        this.memory = Memory.getInstance();
        this.cpu = new CPU();
        loader = new Loader();
    }

    public void loadProgram(byte[] program, int startAddress) {
        loader.writeProgram(program, startAddress);
        this.cpu.setIc(startAddress);
    }

    public void run() {
        cpu.startup();
    }


    // --- Getter/ Setter ---

    public Memory getMemory() {
        return this.memory;
    }

    public CPU getCpu() {
        return this.cpu;
    }

    public Loader getLoader() {
        return this.loader;
    }
}
