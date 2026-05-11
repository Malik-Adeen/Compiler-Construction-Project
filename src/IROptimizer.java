import java.util.*;

public class IROptimizer {

    private IntermediateRepresentation originalIR;
    private IntermediateRepresentation optimizedIR;
    private Map<String, String> tempVariableMap;
    private int optimizationsPerformed;

    public IROptimizer(IntermediateRepresentation ir) {
        this.originalIR = ir;
        this.optimizedIR = new IntermediateRepresentation();
        this.tempVariableMap = new HashMap<>();
        this.optimizationsPerformed = 0;
    }

    public IntermediateRepresentation optimize() {
        if (originalIR == null || originalIR.size() == 0) {
            return optimizedIR;
        }

        identifyTempVariables();

        List<IntermediateRepresentation.Quadruple> instructions = originalIR.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            IntermediateRepresentation.Quadruple quad = instructions.get(i);

            if (isRemovable(quad)) {
                optimizationsPerformed++;
                continue;
            }

            IntermediateRepresentation.Quadruple optimized = eliminateTemporaryVariable(quad);
            if (optimized != null) {
                optimizationsPerformed++;
                optimizedIR.emit(optimized.operator, optimized.operand1, optimized.operand2, optimized.result);
            } else {

                optimizedIR.emit(quad.operator, quad.operand1, quad.operand2, quad.result);
            }
        }

        return optimizedIR;
    }

    private void identifyTempVariables() {
        List<IntermediateRepresentation.Quadruple> instructions = originalIR.getInstructions();

        for (int i = 0; i < instructions.size() - 1; i++) {
            IntermediateRepresentation.Quadruple curr = instructions.get(i);
            IntermediateRepresentation.Quadruple next = instructions.get(i + 1);

            if (isTemporaryVariable(curr.result) && "=".equals(next.operator)) {
                if (curr.result.equals(next.operand1) && next.operand2 == null) {
                    tempVariableMap.put(curr.result, curr.operator + "|" + curr.operand1 + "|" + curr.operand2);
                }
            }
        }
    }

    private boolean isRemovable(IntermediateRepresentation.Quadruple quad) {

        if ("*".equals(quad.operator)) {
            if ((isOne(quad.operand1) && quad.result.equals(quad.operand2)) ||
                    (isOne(quad.operand2) && quad.result.equals(quad.operand1))) {
                return true;
            }
        }

        if ("+".equals(quad.operator)) {
            if ((isZero(quad.operand1) && quad.result.equals(quad.operand2)) ||
                    (isZero(quad.operand2) && quad.result.equals(quad.operand1))) {
                return true;
            }
        }

        if ("-".equals(quad.operator)) {
            if (isZero(quad.operand2) && quad.result.equals(quad.operand1)) {
                return true;
            }
        }

        if ("/".equals(quad.operator)) {
            if (isOne(quad.operand2) && quad.result.equals(quad.operand1)) {
                return true;
            }
        }

        return false;
    }

    private IntermediateRepresentation.Quadruple eliminateTemporaryVariable(
            IntermediateRepresentation.Quadruple quad) {
        if (!"=".equals(quad.operator)) {
            return null;
        }

        if (isTemporaryVariable(quad.operand1) && tempVariableMap.containsKey(quad.operand1)) {
            String[] parts = tempVariableMap.get(quad.operand1).split("\\|");
            if (parts.length == 3) {

                return new IntermediateRepresentation.Quadruple(
                        quad.index,
                        parts[0],
                        parts[1],
                        parts[2],
                        quad.result);
            }
        }

        return null;
    }

    private String foldConstants(String operand1, String operator, String operand2) {
        if (isNumeric(operand1) && isNumeric(operand2)) {
            try {
                double val1 = Double.parseDouble(operand1);
                double val2 = Double.parseDouble(operand2);
                double result = 0;

                switch (operator) {
                    case "+":
                        result = val1 + val2;
                        break;
                    case "-":
                        result = val1 - val2;
                        break;
                    case "*":
                        result = val1 * val2;
                        break;
                    case "/":
                        if (val2 != 0) {
                            result = val1 / val2;
                        }
                        break;
                    default:
                        return null;
                }

                if (result == (long) result) {
                    return String.valueOf((long) result);
                } else {
                    return String.valueOf(result);
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isZero(String value) {
        return "0".equals(value) || "0.0".equals(value);
    }

    private boolean isOne(String value) {
        return "1".equals(value) || "1.0".equals(value);
    }

    private boolean isNumeric(String value) {
        if (value == null)
            return false;
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isTemporaryVariable(String varName) {
        return varName != null && varName.startsWith("T");
    }

    public IntermediateRepresentation getOptimizedIR() {
        return optimizedIR;
    }

    public String getOptimizationReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nOPTIMIZATION REPORT\n");
        sb.append("=".repeat(70) + "\n");
        sb.append("Original IR Instructions: ").append(originalIR.size()).append("\n");
        sb.append("Optimized IR Instructions: ").append(optimizedIR.size()).append("\n");
        sb.append("Instructions Removed: ").append(originalIR.size() - optimizedIR.size()).append("\n");
        sb.append("Optimizations Performed: ").append(optimizationsPerformed).append("\n");

        if (originalIR.size() > 0) {
            double reductionPercent = ((originalIR.size() - optimizedIR.size()) * 100.0) / originalIR.size();
            sb.append("Code Reduction: ").append(String.format("%.2f%%", reductionPercent)).append("\n");
        }

        return sb.toString();
    }

    public void printOptimizationReport() {
        System.out.print(getOptimizationReport());
    }
}
