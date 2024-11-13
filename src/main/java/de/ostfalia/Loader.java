package de.ostfalia;

public class Loader {
    private Memory memory;

    public Loader(){
        this.memory = Memory.getInstance();
    }

    public void writeProgram(byte[] program, int startAddress) {

        int counter = startAddress;

        for (byte val : program) {
            memory.write(counter, val);
            counter++;
        }
    }
}
