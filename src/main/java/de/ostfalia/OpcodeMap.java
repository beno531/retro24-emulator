package de.ostfalia;

import java.util.HashMap;
import java.util.Map;

public class OpcodeMap {
    private Map<Integer, Runnable> opcodes;

    public OpcodeMap(CPU cpu) {
        opcodes = new HashMap<>();
        initialize(cpu);
    }

    private void initialize(CPU cpu) {

        opcodes.put(0xA9, cpu::ldaImmediate); // Opcode für LDA #immediate


        opcodes.put(0x00, cpu::ldaImmediate); // NUL ($00, 1-Byte-OP): Prozessor tut nichts
        opcodes.put(0x01, cpu::ldaImmediate); // MAR ($01, 3-Byte-OP): Lädt AR mit den nächsten beiden Bytes.
        opcodes.put(0x02, cpu::ldaImmediate); // SIC ($02, 1-Byte-OP): Speichert IC an die im AR angegebene Adresse.
        opcodes.put(0x03, cpu::ldaImmediate); // RAR ($03, 1-Byte-OP): R1/R2 werden ins AR kopiert.
        opcodes.put(0x04, cpu::ldaImmediate); // AAR ($04, 1-Byte-OP): Addiert R0 aufs AR, bei Überlauf geht Übertrag verloren.
        opcodes.put(0x05, cpu::ldaImmediate); // IR0 ($05, 1-Byte-OP): Erhöht den Wert von R0 um 1, allerdings nicht über $FF hinaus.
        opcodes.put(0x06, cpu::ldaImmediate); // A01 ($06, 1-Byte-OP): Addiert R0 auf R1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodes.put(0x07, cpu::ldaImmediate); // DR0 ($07, 1-Byte-OP): Erniedrigt den Wert von R0 um 1, allerdings nicht unter $00.
        opcodes.put(0x08, cpu::ldaImmediate); // S01 ($08, 1-Byte-OP): Subtrahiert R0 von R1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodes.put(0x09, cpu::ldaImmediate); // X12 ($09, 1-Byte-OP): Vertauscht die Inhalte von R1 und R2.
        opcodes.put(0x10, cpu::ldaImmediate); // X01 ($10, 1-Byte-OP): Vertauscht die Inhalte von R0 und R1.

        opcodes.put(0x11, cpu::ldaImmediate); // JMP ($11, 1-Byte-OP): Springt zu der in AR angegebenen Adresse.
        opcodes.put(0x12, cpu::ldaImmediate); // SR0 ($12, 1-Byte-OP): Speichert R0 an die in AR angegebene Adresse.
        opcodes.put(0x13, cpu::ldaImmediate); // SRW ($13, 1-Byte-OP): Speichert R1 an die in AR angegebene Adresse, ferner R2 an die Adresse dahinter.
        opcodes.put(0x14, cpu::ldaImmediate); // LR0 ($14, 1-Byte-OP): Lädt R0 aus der in AR angegebenen Adresse.
        opcodes.put(0x15, cpu::ldaImmediate); // LRW ($15, 1-Byte-OP): Lädt R1 aus der in AR angegebenen Adresse, ferner R2 aus der Adresse dahinter.
        opcodes.put(0x16, cpu::ldaImmediate); // TAW ($16, 1-Byte-OP): AR wird nach R1/R2 kopiert.
        opcodes.put(0x17, cpu::ldaImmediate); // MR0 ($17, 2-Byte-OP): Das nachfolgende Byte wird nach R0 geschrieben.
        opcodes.put(0x18, cpu::ldaImmediate); // MRW ($18, 3-Byte-OP): Die nachfolgenden 2 Bytes werden nach R1 und R2 geschrieben.
        opcodes.put(0x19, cpu::ldaImmediate); // JZ0 ($19, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0=$00 ist.
        opcodes.put(0x20, cpu::ldaImmediate); // JGW ($20, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1 > R2 ist.
        opcodes.put(0x21, cpu::ldaImmediate); // JEW ($21, 1-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R1=R2 ist.
        opcodes.put(0x22, cpu::ldaImmediate); // OR0 ($22, 2-Byte-OP): Speichert in R0 das logische ODER aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodes.put(0x23, cpu::ldaImmediate); // AN0 ($23, 2-Byte-OP): Speichert in R0 das logische UND aus dem aktuellen Wert von R0 und dem nachfolgenden Byte.
        opcodes.put(0x24, cpu::ldaImmediate); // JE0 ($24, 2-Byte-OP): Springt zu der in AR angegebenen Adresse, falls R0 gleich dem nachfolgenden Byte ist.
        opcodes.put(0x25, cpu::ldaImmediate); // C01 ($25, 1-Byte-OP): Kopiert R0 nach R1.
        opcodes.put(0x26, cpu::ldaImmediate); // C02 ($26, 1-Byte-OP): Kopiert R0 nach R2.
        opcodes.put(0x27, cpu::ldaImmediate); // IRW ($27, 1-Byte-OP): Erhöht den Wert von R1 um 1. Bei Überlauf wird R2 um 1 erhöht. Läuft dabei wiederum R2 über, werden R1 und R2 zu $FF.
        opcodes.put(0x28, cpu::ldaImmediate); // DRW ($28, 1-Byte-OP): Erniedrigt den Wert von R1 um 1. Falls eine negative Zahl entsteht, enthält R1 dann den Betrag der negativen Zahl. Ferner wird dann R2 um 1 erniedrigt. Tritt dabei ein Unterlauf von R2 auf, werden R1 und R2 zu $00.
        opcodes.put(0x29, cpu::ldaImmediate); // X03 ($29, 1-Byte-OP): Vertauscht die Inhalte von R0 und R3.

        opcodes.put(0x2A, cpu::ldaImmediate); // C03 ($2A, 1-Byte-OP): Kopiert R0 nach R3.
        opcodes.put(0x2B, cpu::ldaImmediate); // C30 ($2B, 1-Byte-OP): Kopiert R3 nach R0.
        opcodes.put(0x2C, cpu::ldaImmediate); // PL0 ($2C, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „links“ (entspricht Teilen durch 2 ohne Rest)
        opcodes.put(0x2D, cpu::ldaImmediate); // PR0 ($2D, 1-Byte-OP): Schiebt die Bits in R0 um ein Bit nach „rechts“ (entspricht Multiplikation mit 2 ohne Übertrag).
        opcodes.put(0xFF, cpu::ldaImmediate); // HLT ($FF, 1-Byte-OP): Prozessor hält an
    }

    public Runnable getInstruction(int opcode) {
        return opcodes.get(opcode);
    }
}


