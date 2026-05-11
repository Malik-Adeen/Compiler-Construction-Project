import java.util.*;

public class AssemblyCodeGenerator {

    private IntermediateRepresentation ir;
    private SymbolTable symbolTable;
    private List<String> assemblyCode;
    private Map<String, String> variableLocations;
    private int memoryOffset;

    private static final String[] REGISTERS = { "R0", "R1", "R2", "R3" };
    private Map<String, String> tempToRegister;
    private int registerCounter;

    public AssemblyCodeGenerator(IntermediateRepresentation ir, SymbolTable symbolTable) {
        this.ir = ir;
        this.symbolTable = symbolTable;
        this.assemblyCode = new ArrayList<>();
        this.variableLocations = new HashMap<>();
        this.tempToRegister = new HashMap<>();
        this.memoryOffset = 0;
        this.registerCounter = 0;
    }

    public void generate() {
        if (ir == null || ir.size() == 0) {
            addInstruction("; Empty program");
            return;
        }

        generatePrologue();

        allocateMemory();

        for (IntermediateRepresentation.Quadruple quad : ir.getInstructions()) {
            generateAssemblyForQuadruple(quad);
        }

        generateEpilogue();
    }

    private void generatePrologue() {
        addInstruction("; ====== ASSEMBLY CODE ======");
        addInstruction("; Function prologue");
        addInstruction("PUSH BP            ; Save base pointer");
        addInstruction("MOV BP, SP         ; Set up new base pointer");
        addInstruction("SUB SP, 128        ; Allocate stack space");
        addInstruction("");
    }

    private void generateEpilogue() {
        addInstruction("");
        addInstruction("; Function epilogue");
        addInstruction("MOV SP, BP         ; Restore stack pointer");
        addInstruction("POP BP             ; Restore base pointer");
        addInstruction("RET                ; Return from function");
    }

    private void allocateMemory() {
        addInstruction("; Variable allocation");

        Collection<SymbolTable.Symbol> symbols = ((SymbolTable) symbolTable).getAllSymbols();

        for (SymbolTable.Symbol sym : symbols) {
            String location = "[BP - " + (memoryOffset + 4) + "]";
            variableLocations.put(sym.lexeme, location);
            memoryOffset += 4;
            addInstruction("; " + sym.lexeme + " : " + sym.type + " at " + location);
        }

        addInstruction("");
    }

    private void generateAssemblyForQuadruple(IntermediateRepresentation.Quadruple quad) {
        switch (quad.operator) {
            case "=":
                generateAssignment(quad);
                break;
            case "+":
                generateAddition(quad);
                break;
            case "-":
                generateSubtraction(quad);
                break;
            case "*":
                generateMultiplication(quad);
                break;
            case "/":
                generateDivision(quad);
                break;
            case "PRINT":
                generatePrint(quad);
                break;
            case "LABEL":
                generateLabel(quad);
                break;
            case "JMP":
                generateJump(quad);
                break;
            case "==":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "<>":
                generateConditionalJump(quad);
                break;
            default:
                addInstruction("; Unsupported operation: " + quad.operator);
        }
    }

    private void generateAssignment(IntermediateRepresentation.Quadruple quad) {
        String source = getOperandLocation(quad.operand1);
        String dest = getVariableLocation(quad.result);

        addInstruction("; Assignment: " + quad.result + " = " + quad.operand1);

        if (isLiteral(quad.operand1)) {
            addInstruction("MOV R0, " + quad.operand1);
            addInstruction("MOV " + dest + ", R0");
        } else {
            addInstruction("MOV R0, " + source);
            addInstruction("MOV " + dest + ", R0");
        }
        addInstruction("");
    }

    private void generateAddition(IntermediateRepresentation.Quadruple quad) {
        String op1 = getOperandLocation(quad.operand1);
        String op2 = getOperandLocation(quad.operand2);
        String result = getVariableLocation(quad.result);

        addInstruction("; Addition: " + quad.result + " = " + quad.operand1 + " + " + quad.operand2);
        addInstruction("MOV R0, " + op1);
        addInstruction("MOV R1, " + op2);
        addInstruction("ADD R0, R0, R1");
        addInstruction("MOV " + result + ", R0");
        addInstruction("");
    }

    private void generateSubtraction(IntermediateRepresentation.Quadruple quad) {
        String op1 = getOperandLocation(quad.operand1);
        String op2 = getOperandLocation(quad.operand2);
        String result = getVariableLocation(quad.result);

        addInstruction("; Subtraction: " + quad.result + " = " + quad.operand1 + " - " + quad.operand2);
        addInstruction("MOV R0, " + op1);
        addInstruction("MOV R1, " + op2);
        addInstruction("SUB R0, R0, R1");
        addInstruction("MOV " + result + ", R0");
        addInstruction("");
    }

    private void generateMultiplication(IntermediateRepresentation.Quadruple quad) {
        String op1 = getOperandLocation(quad.operand1);
        String op2 = getOperandLocation(quad.operand2);
        String result = getVariableLocation(quad.result);

        addInstruction("; Multiplication: " + quad.result + " = " + quad.operand1 + " * " + quad.operand2);
        addInstruction("MOV R0, " + op1);
        addInstruction("MOV R1, " + op2);
        addInstruction("MUL R0, R0, R1");
        addInstruction("MOV " + result + ", R0");
        addInstruction("");
    }

    private void generateDivision(IntermediateRepresentation.Quadruple quad) {
        String op1 = getOperandLocation(quad.operand1);
        String op2 = getOperandLocation(quad.operand2);
        String result = getVariableLocation(quad.result);

        addInstruction("; Division: " + quad.result + " = " + quad.operand1 + " / " + quad.operand2);
        addInstruction("MOV R0, " + op1);
        addInstruction("MOV R1, " + op2);
        addInstruction("DIV R0, R0, R1");
        addInstruction("MOV " + result + ", R0");
        addInstruction("");
    }

    private void generatePrint(IntermediateRepresentation.Quadruple quad) {
        String operand = getOperandLocation(quad.operand1);

        addInstruction("; Print: " + quad.operand1);
        addInstruction("MOV R0, " + operand);
        addInstruction("CALL PRINT_INT");
        addInstruction("");
    }

    private void generateLabel(IntermediateRepresentation.Quadruple quad) {
        addInstruction(quad.operand1 + ":");
    }

    private void generateJump(IntermediateRepresentation.Quadruple quad) {
        addInstruction("JMP " + quad.operand1);
        addInstruction("");
    }

    private void generateConditionalJump(IntermediateRepresentation.Quadruple quad) {
        String op1 = getOperandLocation(quad.operand1);
        String op2 = getOperandLocation(quad.operand2);

        addInstruction("; Condition: " + quad.operand1 + " " + quad.operator + " " + quad.operand2);
        addInstruction("MOV R0, " + op1);
        addInstruction("MOV R1, " + op2);
        addInstruction("CMP R0, R1");

        switch (quad.operator) {
            case "==":
                addInstruction("JE " + quad.result);
                break;
            case "<":
                addInstruction("JL " + quad.result);
                break;
            case ">":
                addInstruction("JG " + quad.result);
                break;
            case "<=":
                addInstruction("JLE " + quad.result);
                break;
            case ">=":
                addInstruction("JGE " + quad.result);
                break;
            case "<>":
                addInstruction("JNE " + quad.result);
                break;
        }
        addInstruction("");
    }

    private String getOperandLocation(String operand) {
        if (operand == null) {
            return "0";
        }

        if (isLiteral(operand)) {
            return operand;
        }

        if (isTemporaryVariable(operand)) {

            if (!tempToRegister.containsKey(operand)) {
                tempToRegister.put(operand, REGISTERS[registerCounter % REGISTERS.length]);
                registerCounter++;
            }
            return tempToRegister.get(operand);
        }

        return getVariableLocation(operand);
    }

    private String getVariableLocation(String varName) {
        if (varName == null) {
            return "[BP - 4]";
        }

        if (variableLocations.containsKey(varName)) {
            return variableLocations.get(varName);
        }

        String location = "[BP - " + (memoryOffset + 4) + "]";
        variableLocations.put(varName, location);
        memoryOffset += 4;

        return location;
    }

    private boolean isLiteral(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return value.startsWith("\"") && value.endsWith("\"");
        }
    }

    private boolean isTemporaryVariable(String varName) {
        return varName != null && varName.startsWith("T");
    }

    private void addInstruction(String instruction) {
        assemblyCode.add(instruction);
    }

    public List<String> getAssemblyCode() {
        return new ArrayList<>(assemblyCode);
    }

    public void printAssemblyCode() {
        System.out.println("\nASSEMBLY CODE (x86 CISC)");
        System.out.println("=".repeat(70));

        for (String instruction : assemblyCode) {
            System.out.println(instruction);
        }

        System.out.println("=".repeat(70));
    }

    public String exportAssemblyCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nASSEMBLY CODE (x86 CISC)\n");
        sb.append("=".repeat(70)).append("\n");

        for (String instruction : assemblyCode) {
            sb.append(instruction).append("\n");
        }

        return sb.toString();
    }

    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAssembly Code Statistics:\n");
        sb.append("Total Instructions: ").append(assemblyCode.size()).append("\n");
        sb.append("Memory Allocated: ").append(memoryOffset).append(" bytes\n");
        sb.append("Registers Used: ").append(Math.min(registerCounter, REGISTERS.length)).append("\n");
        return sb.toString();
    }
}
