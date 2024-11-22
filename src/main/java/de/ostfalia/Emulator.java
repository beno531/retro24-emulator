package de.ostfalia;

public class Emulator {

    private Memory memory;
    private CPU cpu;
    private Loader loader;

    public Emulator(){
        this.memory = new Memory();

        this.cpu = new CPU();
        cpu.connectMemory(memory);

        loader = new Loader();
        loader.connectMemory(memory);
    }

    public void loadProgram(int[] program) {
        loader.writeProgram(program);
    }
    public void loadProgram(String filePath) {
        loader.writeProgram(filePath);
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
