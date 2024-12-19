package de.ostfalia;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

public class Emulator {

    private Memory memory;
    private CPU cpu;

    private PPU ppu;
    private Loader loader;

    public Emulator(){
        this.memory = new Memory();

        this.cpu = new CPU();
        cpu.connectMemory(memory);


        JFrame frame = new JFrame("PPU Emulation");
        Canvas canvas = new Canvas();
        canvas.setSize(640, 640); // 64x64 Pixel mit 10x10 Pixeln Größe
        frame.add(canvas);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        this.ppu = new PPU(canvas, memory);
        //ppu.connectMemory(memory);

        //ppu.initializememory();

        loader = new Loader();
        loader.connectMemory(memory);
    }

    public void loadProgram(int[] program) {
        loader.writeProgram(program);
    }
    public void loadProgram(String filePath) {
        loader.writeProgram(filePath);
    }

    public void start() throws InterruptedException {






        while (true) {

            if (!cpu.getHlt()){
                cpu.clock();
            }

            ppu.clock();

            Thread.sleep(100);
        }
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
