package de.ostfalia;

import java.awt.*;
import java.awt.event.*;

public class PPU{

    private enum State {
        ZUSTAND_1, ZUSTAND_2
    }

    private byte[] memory1 = new byte[memory1_SIZE]; // Speicher des Emulators
    private long lastUpdate = 0;
    private State currentState = State.ZUSTAND_1;
    private int x = 0;




    // Needed
    private static final int memory1_SIZE = 0x10000; // 64 KB Speicher
    private static final int SCREEN_WIDTH = 64;
    private static final int SCREEN_HEIGHT = 64;
    private static final int PIXEL_SIZE = 10; // Größe eines Pixels in der Anzeige
    private Memory memory;



    private Canvas canvas;
    private int pixelSize = 10; // Größe eines Pixels auf dem Canvas





    public PPU(Canvas canvas, Memory memory) {

        this.memory = memory;

        this.canvas = canvas;
    }



    public void clock(){
        if(memory.read(0x000A) == 0x01){
            draw();
            memory.write(0x000A, 0x00);
        }
    }


    private void draw() {
        Graphics g = canvas.getGraphics();

        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                // Adresse im Speicher berechnen
                int index = y * 64 + x;

                // Dunkel/Hell aus $E000
                boolean isBright = (memory.read(0xE000 + index / 8) & (1 << (7 - (index % 8)))) != 0;

                // Monochrom/Farbig aus $F000
                boolean isColor = (memory.read(0xF000 + index / 8) & (1 << (7 - (index % 8)))) != 0;

                // Farbe basierend auf den Bits bestimmen
                Color color = determineColor(isBright, isColor);

                // Pixel auf dem Canvas zeichnen
                g.setColor(color);
                g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
        }
    }


    private Color determineColor(boolean isBright, boolean isColor) {
        if (isBright && isColor) {
            return Color.YELLOW; // Hell und farbig
        } else if (isBright) {
            return Color.WHITE; // Hell und monochrom
        } else if (isColor) {
            return Color.BLUE; // Dunkel und farbig
        } else {
            return Color.BLACK; // Dunkel und monochrom
        }
    }


    /*private void update() {

        switch (currentState) {
            case ZUSTAND_1:
                if (x < 8) {
                    //memory[x] = (byte) 0x00; // Setze den Speicher auf 0
                    memory.write(0xE000 + x, 0xFF);
                    memory.write(0xF000 + x, 0xFF);
                    ++x;
                }
                if (x == 8) {
                    x = 0;
                    currentState = State.ZUSTAND_2;
                }
                break;
            case ZUSTAND_2:
                if (x < 8) {
                    //memory[x] = (byte) 0xFF; // Setze den Speicher auf 1
                    memory.write(0xE000 + x, 0xDD);
                    memory.write(0xF000 + x, 0xAA);
                    ++x;
                }
                if (x == 8) {
                    x = 0;
                    currentState = State.ZUSTAND_1;
                }
                break;
        }
    }*/

    public void initializememory() {
        // Fülle den Speicher mit Testdaten für eine einfache Darstellung
        for (int i = 0; i < 64 * 64 / 8; i++) {
            memory.write(0xE000 + i, 0xFF); // Alle Pixel hell
            memory.write(0xF000 + i, 0xAA); // Abwechselnd farbig/monochrom
        }


        //memory.write(0xE000, 0xDD);
        //memory.write(0xF000, 0xAA);
    }
}
