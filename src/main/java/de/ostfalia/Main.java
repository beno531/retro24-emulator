package de.ostfalia;

public class Main {

    public static void main(String[] args) {
        Emulator emulator = new Emulator();

        emulator.loadProgram("src/resources/milestone1_easy.bin");
        //emulator.loadProgram("src/resources/milestone1_advanced.bin");

        emulator.run();
    }

}
