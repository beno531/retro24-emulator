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
    private Map<Integer, Runnable> opcodeMap = new HashMap<>();

    public CPU() {
        this.memory = Memory.getInstance();

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


        opcodeMap.put(0x11, this::jmp); // JMP ($11, 1-Byte-OP): Springt zu der in AR angegebenen Adresse.
        opcodeMap.put(0x12, this::sr0); // SR0 ($12, 1-Byte-OP): Speichert R0 an die in AR angegebene Adresse.
        opcodeMap.put(0x13, this::srw); // SRW ($13, 1-Byte-OP): Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
        opcodeMap.put(0x14, this::lr0); // LR0 ($14, 1-Byte-OP): Lädt R0 aus der in AR angegebenen Adresse.
        opcodeMap.put(0x15, this::lrw); // LRW ($15, 1-Byte-OP): Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
        opcodeMap.put(0x16, this::taw); // TAW ($16, 1-Byte-OP): AR wird nach R1/R2 kopiert.

        opcodeMap.put(0x17, this::mr0); // MR0 ($17, 2-Byte-OP): Das nachfolgende Byte wird nach R0 geschrieben.
        opcodeMap.put(0x18, this::mrw); // MRW ($18, 3-Byte-OP): Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
        opcodeMap.put(0x19, this::jz0); // JZ0 ($19, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
        opcodeMap.put(0x20, this::jgw); // JGW ($20, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
        opcodeMap.put(0x21, this::jew); // JEW ($21, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
        opcodeMap.put(0x22, this::or0); // OR0 ($22, 2-Byte-OP): Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x23, this::an0); // AN0 ($23, 2-Byte-OP): Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodeMap.put(0x24, this::je0); // JE0 ($24, 2-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
        opcodeMap.put(0x25, this::c01); // C01 ($25, 1-Byte-OP): Kopiert R0 nach R1.
        opcodeMap.put(0x26, this::c02); // C02 ($26, 1-Byte-OP): Kopiert R0 nach R2.

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

            debugOut();

            int opcode = memory.read(ic);

            Runnable instruction = opcodeMap.get(opcode);

            if (instruction != null) {
                instruction.run();
            } else {
                break;
                //throw new IllegalStateException("Unbekannter Opcode: " + opcode);

            }
        }
    }

    private void debugOut(){

        System.out.printf("----------------------------------------- \n");

        System.out.printf("AR: 0x%04X%n", this.ar);
        System.out.printf("IC: 0x%04X%n", this.ic);

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

    public int getR0() {
        return r0;
    }

    public void setR0(int r0) {
        this.r0 = r0;
    }

    public int getR1() {
        return r1;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public int getR2() {
        return r2;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public int getR3() {
        return r3;
    }

    public void setR3(int r3) {
        this.r3 = r3;
    }

    // --- Instructions ---

    public void ldaImmediate() {
        System.out.print("Platzhalter");
    }

    // Prozessor tut nichts
    private void nul(){
        ++ic;
    }

    // Lädt AR mit den nächsten beiden Bytes.
    private void mar(){
        // little endian
        ar = ((memory.read(ic + 2) & 0xFF) << 8) | (memory.read(ic + 1) & 0xFF);
        ic += 3;
    }

    // Speichert IC an die im AR angegebene Adresse.
    private void sic(){
        byte lowByte = (byte) (ic & 0xFF);
        byte highByte = (byte) ((ic >> 8) & 0xFF);
        memory.write(ar, lowByte);
        memory.write(ar + 1, highByte); // TODO: Wird AR auch erhöht? Woher weiß ich, dass aufgeteilt wurde?
        ic += 1;
    }

    // R1/R2 werden ins AR kopiert.
    private void rar(){
        ar = ((r2 & 0xFF) << 8) | (r1 & 0xFF);
        ++ic;
    }

    // Addiert R0 aufs AR, bei Überlauf geht Übertrag verloren.
    private void aar(){
        ar = Math.min(ar + r0, 0xFFFF);
        ++ic;
    }

    // Erhöht den Wert von R0 um 1, allerdings nicht über $FF hinaus.
    private void ir0() {
        r0 = Math.min(++r0, 0xFF);
        ++ic;
    }

    // Addiert R0 auf R1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
    private void a01(){
        int x = r1 + r0;
        if(x > 0xFF){
            r1 = 0xFF;
            r2 = Math.min(++r2, 0xFF);
        }
        else {
            r1 = x;
        }
        ++ic;
    }

    // Erniedrigt den Wert von R0 um 1, allerdings nicht unter $00.
    private void dr0(){
        r0 = Math.max(--r0, 0x00);
        ++ic;
    }

    // Subtrahiert R0 von R1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
    private void s01(){
        int x = (byte) r1 - (byte) r0;

        if (x < 0) {
            r1 = Math.abs(x);

            --r2;
            if (r2 < 0) {
                r1 = 0x00;
                r2 = 0x00;
            }
        } else {
            r1 = x;
        }
        ++ic;
    }

    // Vertauscht die Inhalte von R1 und R2.
    private void x12(){
        int temp = r1;
        r1 = r2;
        r2 = temp;
        ++ic;
    }

    // Vertauscht die Inhalte von R0 und R1.
    private void x01(){
        int temp = r0;
        r0 = r1;
        r1 = temp;
        ++ic;
    }

    // Springt zu der in AR angegebenen Adresse.
    private void jmp(){
        ic = memory.read(ar); // TODO: Test nochmal anschauen
    }

    // Speichert R0 an die in AR angegebene Adresse.
    private void sr0(){
        memory.write(ar, r0);
        ++ic;
    }

    // Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
    private void srw(){
        memory.write(ar, r1);
        memory.write(ar + 1, r2); // TODO: AR Überlauf?
        ++ic;
    }

    // Lädt R0 aus der in AR angegebenen Adresse.
    private void lr0(){
        r0 = memory.read(ar);
        ++ic;
    }

    // Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
    private void lrw(){
        r1 = memory.read(ar);
        r2 = memory.read(ar + 1);
        ++ic;
    }

    // AR wird nach R1/R2 kopiert.
    private void taw(){
        byte lowByte = (byte) (ar & 0xFF);
        byte highByte = (byte) ((ar >> 8) & 0xFF);
        r1 = lowByte;
        r2 = highByte;
        ++ic;
    }

    // Das nachfolgende Byte wird nach R0 geschrieben.
    private void mr0(){
        r0 = memory.read(ic + 1);
        ic += 2;
    }

    // Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
    private void mrw(){
        r1 = memory.read(ic + 1);
        r2 = memory.read(ic + 2);
        ic += 3;
    }

    // Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
    private void jz0(){
        if(r0 == 0x00){
            ic = memory.read(ar);
        }
        else {
            ++ic;
        }
    }

    // Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
    private void jgw(){
        if(r1 > r2){
            ic = memory.read(ar);
        }
        else {
            ++ic;
        }
    }

    // Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
    private void jew(){
        if(r1 == r2){
            ic = memory.read(ar);
        }
        else {
            ++ic;
        }
    }

    // Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
    private void or0(){
        r0 = r0 | memory.read(ic + 1);
        ic += 2;
    }

    // Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
    private void an0(){
        r0 = r0 & memory.read(ic + 1);
        ic += 2;
    }

    // Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
    private void je0(){

        if(r0 == memory.read(ic + 1)){
            ic = memory.read(ar);
        }
        else {
            ic += 2;
        }
    }

    // Kopiert R0 nach R1.
    private void c01(){
        r1 = r0;
        ++ic;   // TODO: "Schreibt nach" überprüfen, ob wert zurückgesetzt wird
    }

    // Kopiert R0 nach R2.
    private void c02(){
        r2 = r0;
        ++ic; // TODO: "Schreibt nach" überprüfen, ob wert zurückgesetzt wird
    }

    // Erhöht den Wert von R1 um 1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
    private void irw(){

        ++ic;
    }

    // Erniedrigt den Wert von R1 um 1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
    private void drw(){

        ++ic;
    }

    // Vertauscht die Inhalte von R0 und R3.
    private void x03(){

        ++ic;
    }

    // Kopiert R0 nach R3.
    private void c03(){

        ++ic;
    }

    // Kopiert R3 nach R0.
    private void c30(){

        ++ic;
    }

    // Schiebt die Bits in R0 um ein Bit nach „links“ (entspricht Teilen durch 2 ohne Rest)
    private void pl0(){

        ++ic;
    }

    // Schiebt die Bits in R0 um ein Bit nach „rechts“ (entspricht Multiplikation mit 2 ohne Übertrag).
    private void pr0(){

        ++ic;
    }

    // Prozessor hält an
    private void hlt(){

        ++ic;
    }

}