package de.ostfalia;

public class Loader {
    private Memory memory;

    public Loader(){
        this.memory = Memory.getInstance();
    }

    public void writeProgram(int[] program) {

        int counter = 0X0100;

        for (int val : program) {
            memory.write(counter, val);
            counter++;
        }
    }
}
