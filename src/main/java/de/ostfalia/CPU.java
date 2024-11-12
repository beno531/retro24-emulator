package de.ostfalia;


public class CPU {
    // Register
    private int ar = 0x00;  // Akkumulator
    private int ic = 0x00;  // Instruction Counter
    private int r0 = 0x00;
    private int r1 = 0x00;
    private int r2 = 0x00;
    private int r3 = 0x00;

    private Memory memory;

    private OpcodeMap opcodeMap;

    public CPU() {
        this.memory = Memory.getInstance();
        this.opcodeMap = new OpcodeMap(this);
    }

    public void executeInstruction() {

        int opcode = memory.read(ic);

        Runnable instruction = opcodeMap.getInstruction(opcode);

        if (instruction != null) {
            instruction.run();
        } else {
            throw new IllegalStateException("Unbekannter Opcode: " + opcode);
        }
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
        System.out.print("Hallo");
    }
}