package de.ostfalia;

import java.util.HashMap;
import java.util.Map;
public class CPU {
    // Register
    private int ar = 0x0000;  // Address Register
    private int ic = 0x0000;  // Instruction Counter
    private int r0 = 0x00;
    private int r1 = 0x00;
    private int r2 = 0x00;
    private int r3 = 0x00;

    private Memory memory;

    //private OpcodeMap opcodeMap;
    private Map<Integer, Runnable> opcodeMap = new HashMap<>();

    public CPU() {
        this.memory = Memory.getInstance();


        //this.opcodeMap = new OpcodeMap(this);
        opcodeMap.put(0x00, this::nul); // NUL ($00, 1-Byte-OP): Prozessor tut nichts
        opcodeMap.put(0x01, this::mar); // MAR ($01, 3-Byte-OP): Lädt AR mit den nächsten beiden Bytes.
        opcodeMap.put(0x02, this::sic); // SIC ($02, 1-Byte-OP): Speichert IC an die im AR angegebene Adresse.
        opcodeMap.put(0x03, this::rar); // RAR ($03, 1-Byte-OP): R1/R2 werden ins AR kopiert.
        opcodeMap.put(0x04, this::aar); // AAR ($04, 1-Byte-OP): Addiert R0 aufs AR, bei Überlauf geht Übertrag verloren.
        opcodeMap.put(0x05, this::ir0); // IR0 ($05, 1-Byte-OP): Erhöht den Wert von R0 um 1, allerdings nicht über $FF hinaus.
        opcodeMap.put(0x06, this::a01); // A01 ($06, 1-Byte-OP): Addiert R0 auf R1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodeMap.put(0x07, this::dr0); // DR0 ($07, 1-Byte-OP): Erniedrigt den Wert von R0 um 1, allerdings nicht unter $00.
        opcodeMap.put(0x08, this::s01); // S01 ($08, 1-Byte-OP): Subtrahiert R0 von R1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodeMap.put(0x09, this::x12); // X12 ($09, 1-Byte-OP): Vertauscht die Inhalte von R1 und R2.
        opcodeMap.put(0x10, this::x01); // X01 ($10, 1-Byte-OP): Vertauscht die Inhalte von R0 und R1.

        opcodeMap.put(0x11, this::ldaImmediate); // JMP ($11, 1-Byte-OP): Springt zu der in AR angegebenen Adresse.
        opcodeMap.put(0x12, this::ldaImmediate); // SR0 ($12, 1-Byte-OP): Speichert R0 an die in AR angegebene Adresse.
        opcodeMap.put(0x13, this::ldaImmediate); // SRW ($13, 1-Byte-OP): Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
        opcodeMap.put(0x14, this::ldaImmediate); // LR0 ($14, 1-Byte-OP): Lädt R0 aus der in AR angegebenen Adresse.
        opcodeMap.put(0x15, this::ldaImmediate); // LRW ($15, 1-Byte-OP): Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
        opcodeMap.put(0x16, this::ldaImmediate); // TAW ($16, 1-Byte-OP): AR wird nach R1/R2 kopiert.
        opcodeMap.put(0x17, this::ldaImmediate); // MR0 ($17, 2-Byte-OP): Das nachfolgende Byte wird nach R0 geschrieben.
        opcodeMap.put(0x18, this::ldaImmediate); // MRW ($18, 3-Byte-OP): Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
        opcodeMap.put(0x19, this::ldaImmediate); // JZ0 ($19, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
        opcodeMap.put(0x20, this::ldaImmediate); // JGW ($20, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
        opcodeMap.put(0x21, this::ldaImmediate); // JEW ($21, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
        opcodeMap.put(0x22, this::ldaImmediate); // OR0 ($22, 2-Byte-OP): Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x23, this::ldaImmediate); // AN0 ($23, 2-Byte-OP): Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x24, this::ldaImmediate); // JE0 ($24, 2-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
        opcodeMap.put(0x25, this::ldaImmediate); // C01 ($25, 1-Byte-OP): Kopiert R0 nach R1.
        opcodeMap.put(0x26, this::ldaImmediate); // C02 ($26, 1-Byte-OP): Kopiert R0 nach R2.
        opcodeMap.put(0x27, this::ldaImmediate); // IRW ($27, 1-Byte-OP): Erhöht den Wert von R1 um 1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodeMap.put(0x28, this::ldaImmediate); // DRW ($28, 1-Byte-OP): Erniedrigt den Wert von R1 um 1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodeMap.put(0x29, this::ldaImmediate); // X03 ($29, 1-Byte-OP): Vertauscht die Inhalte von R0 und R3.

        opcodeMap.put(0x2A, this::ldaImmediate); // C03 ($2A, 1-Byte-OP): Kopiert R0 nach R3.
        opcodeMap.put(0x2B, this::ldaImmediate); // C30 ($2B, 1-Byte-OP): Kopiert R3 nach R0.
        opcodeMap.put(0x2C, this::ldaImmediate); // PL0 ($2C, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „links“ (entspricht Teilen durch 2 ohne Rest)
        opcodeMap.put(0x2D, this::ldaImmediate); // PR0 ($2D, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „rechts“ (entspricht Multiplikation mit 2 ohne Übertrag).
        opcodeMap.put(0xFF, this::ldaImmediate); // HLT ($FF, 1-Byte-OP): Prozessor hält an
    }

    public void startup() {

        while(true){
            int opcode = memory.read(ic);

            //Runnable instruction = opcodeMap.getInstruction(opcode);
            Runnable instruction = opcodeMap.get(opcode);

            if (instruction != null) {
                instruction.run();

                debugOut();
            } else {
                break;
                //throw new IllegalStateException("Unbekannter Opcode: " + opcode);

            }
        }
    }

    private void debugOut(){

        System.out.printf("----------------------------------------- \n");

        System.out.printf("Address Register: 0x%04X%n", this.ar);
        System.out.printf("Instruction Counter: 0x%04X%n", this.ic);

        System.out.printf("R0: 0x%02X%n", this.r0);
        System.out.printf("R1: 0x%02X%n", this.r1);
        System.out.printf("R2: 0x%02X%n", this.r2);
        System.out.printf("R3: 0x%02X%n", this.r3);
    }

    // --- Getter/ Setter ---

    public int getIc() {
        return ic;
    }

    public void setIc(int ic){
        this.ic = ic;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    // --- Instructions ---

    public void ldaImmediate() {
        System.out.print("Platzhalter");
    }

    // Prozessor tut nichts
    private void nul(){

    }

    // Lädt AR mit den nächsten beiden Bytes.
    private void mar(){
        // little endian
        this.ar = ((memory.read(this.ic + 2) & 0xFF) << 8) | (memory.read(this.ic + 1) & 0xFF);

        this.ic += 0x0003;
    }

    private void sic(){

    }

    private void rar(){

    }

    private void aar(){

    }

    private void ir0() {
        this.r0 = (this.r0 + 1) & 0xFF;
    }

    private void a01(){

    }

    private void dr0(){

    }

    private void s01(){

    }

    private void x12(){

    }

    private void x01(){

    }


}