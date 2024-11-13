package de.ostfalia;

import java.lang.reflect.Constructor;

public class Emulator {

    private Memory memory;
    private CPU cpu;

    public Emulator(){
        this.memory = Memory.getInstance();
        this.cpu = new CPU();
    }

    public void loadProgram(byte[] program, int startAddress) {

        Loader loader = new Loader();

        loader.writeProgram(program, startAddress);

        this.cpu.setIc(startAddress);
    }

    public void run() {
        cpu.startup();
    }
}
