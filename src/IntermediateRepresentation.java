import java.util.*;

public class IntermediateRepresentation {

    public static class Quadruple {
        public String operator;

        public String operand1;
        public String operand2;
        public String result;
        public int index;

        public Quadruple(int index, String operator, String operand1, String operand2, String result) {
            this.index = index;
            this.operator = operator;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.result = result;
        }

        @Override
        public String toString() {
            return String.format("[%d] (%s, %s, %s, %s)", index, operator, operand1, operand2, result);
        }

        public String toDetailedString() {
            return String.format("%-4d | %-10s | %-15s | %-15s | %-15s",
                    index, operator, operand1 != null ? operand1 : "-",
                    operand2 != null ? operand2 : "-",
                    result != null ? result : "-");
        }
    }

    private List<Quadruple> instructions;
    private int tempVarCounter;
    private int labelCounter;

    public IntermediateRepresentation() {
        instructions = new ArrayList<>();
        tempVarCounter = 0;
        labelCounter = 0;
    }

    public String newTemp() {
        return "T" + (tempVarCounter++);
    }

    public String newLabel() {
        return "L" + (labelCounter++);
    }

    public int emit(String operator, String operand1, String operand2, String result) {
        Quadruple quad = new Quadruple(instructions.size(), operator, operand1, operand2, result);
        instructions.add(quad);
        return instructions.size() - 1;
    }

    public Quadruple getQuadruple(int index) {
        if (index >= 0 && index < instructions.size()) {
            return instructions.get(index);
        }
        return null;
    }

    public List<Quadruple> getInstructions() {
        return instructions;
    }

    public int size() {
        return instructions.size();
    }

    public void print() {
        System.out.println("\nINTERMEDIATE REPRESENTATION (THREE-ADDRESS CODE)");
        System.out.println("=".repeat(70) + "\n");
        System.out.println(String.format("%-4s | %-10s | %-15s | %-15s | %-15s",
                "#", "OPERATOR", "OPERAND1", "OPERAND2", "RESULT"));
        System.out.println("-".repeat(70));

        for (Quadruple quad : instructions) {
            System.out.println(quad.toDetailedString());
        }

    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Intermediate Representation Summary:\n");
        sb.append("Total Instructions: ").append(instructions.size()).append("\n");
        sb.append("Temporary Variables: ").append(tempVarCounter).append("\n");
        sb.append("Labels: ").append(labelCounter).append("\n");
        return sb.toString();
    }

    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append("THREE-ADDRESS CODE (QUADRUPLE FORMAT)\n");
        sb.append("=".repeat(70)).append("\n");

        for (Quadruple quad : instructions) {
            sb.append(quad.toString()).append("\n");
        }

        return sb.toString();
    }

    public void clear() {
        instructions.clear();
        tempVarCounter = 0;
        labelCounter = 0;
    }
}
