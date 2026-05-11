import java.util.*;

public class SemanticAnalyzer {

    private SymbolTable symbolTable;
    private List<String> semanticErrors;
    private List<String> semanticWarnings;
    private int errorCount;
    private int warningCount;

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.semanticErrors = new ArrayList<>();
        this.semanticWarnings = new ArrayList<>();
        this.errorCount = 0;
        this.warningCount = 0;
    }

    public boolean analyze(SimpleNode rootNode) {
        if (rootNode == null) {
            addError("Root node is null");
            return false;
        }

        semanticErrors.clear();
        semanticWarnings.clear();
        errorCount = 0;
        warningCount = 0;

        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) rootNode.jjtGetChild(i);
            analyzeStatement(child);
        }

        return errorCount == 0;
    }

    private void analyzeStatement(SimpleNode stmtNode) {
        if (stmtNode == null) {
            return;
        }

        String nodeName = stmtNode.toString().split("\\[")[0];

        if ("Assignment".equals(nodeName) || "AssignmentStatement".equals(nodeName)) {
            analyzeAssignment(stmtNode);
        } else if ("Output".equals(nodeName) || "OutStringStatement".equals(nodeName)) {
            analyzeOutput(stmtNode);
        } else if ("Loop".equals(nodeName) || "LoopIfStatement".equals(nodeName)) {
            analyzeLoop(stmtNode);
        } else if ("Switch".equals(nodeName) || "SwitchForStatement".equals(nodeName)) {
            analyzeSwitch(stmtNode);
        }
    }

    private void analyzeAssignment(SimpleNode assignNode) {
        if (assignNode.value == null) {
            addError("Assignment: variable name is null");
            return;
        }

        String varName = (String) assignNode.value;

        if (!symbolTable.containsKey(varName)) {
            addError("Variable '" + varName + "' is not declared");
            return;
        }

        String varType = symbolTable.getType(varName);

        if (assignNode.jjtGetNumChildren() > 0) {
            SimpleNode exprNode = (SimpleNode) assignNode.jjtGetChild(0);
            String exprType = analyzeExpression(exprNode);

            if (exprType != null && !exprType.equals("unknown") && !exprType.equals(varType)) {
                addError("Type mismatch in assignment: cannot assign " + exprType + " to " + varType);
            }
        }
    }

    private void analyzeOutput(SimpleNode outputNode) {
        if (outputNode.jjtGetNumChildren() > 0) {
            SimpleNode exprNode = (SimpleNode) outputNode.jjtGetChild(0);
            String exprType = analyzeExpression(exprNode);

            if (exprType != null && exprType.equals("unknown")) {
                addWarning("Output expression has unknown type");
            }
        }
    }

    private void analyzeLoop(SimpleNode loopNode) {
        if (loopNode.jjtGetNumChildren() >= 1) {
            SimpleNode condNode = (SimpleNode) loopNode.jjtGetChild(0);
            analyzeCondition(condNode);
        }

        for (int i = 1; i < loopNode.jjtGetNumChildren(); i++) {
            SimpleNode stmtNode = (SimpleNode) loopNode.jjtGetChild(i);
            analyzeStatement(stmtNode);
        }
    }

    private void analyzeSwitch(SimpleNode switchNode) {
        if (switchNode.value == null) {
            addError("Switch: variable name is null");
            return;
        }

        String switchVar = (String) switchNode.value;

        if (!symbolTable.containsKey(switchVar)) {
            addError("Switch variable '" + switchVar + "' is not declared");
            return;
        }

        String switchVarType = symbolTable.getType(switchVar);

        if (!switchVarType.equals("int") && !switchVarType.equals("string")) {
            addError("Switch variable must be of type int or string, got " + switchVarType);
        }

        for (int i = 0; i < switchNode.jjtGetNumChildren(); i++) {
            SimpleNode caseNode = (SimpleNode) switchNode.jjtGetChild(i);
            analyzeCase(caseNode, switchVarType);
        }
    }

    private void analyzeCase(SimpleNode caseNode, String switchVarType) {

        for (int i = 0; i < caseNode.jjtGetNumChildren(); i++) {
            SimpleNode stmtNode = (SimpleNode) caseNode.jjtGetChild(i);
            analyzeStatement(stmtNode);
        }
    }

    private void analyzeCondition(SimpleNode condNode) {
        if (condNode == null) {
            addError("Condition is null");
            return;
        }

        if (condNode.value != null) {
            String val = (String) condNode.value;
            if (!symbolTable.containsKey(val)) {

                if (!isLiteral(val)) {
                    addWarning("Condition variable '" + val + "' may not be defined");
                }
            }
        }

        for (int i = 0; i < condNode.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) condNode.jjtGetChild(i);
            analyzeExpression(child);
        }
    }

    private String analyzeExpression(SimpleNode exprNode) {
        if (exprNode == null) {
            return "unknown";
        }

        String nodeType = exprNode.getType();
        if (nodeType != null && !nodeType.isEmpty()) {
            return nodeType;
        }

        if (exprNode.value != null) {
            String val = (String) exprNode.value;
            if (isNumericLiteral(val)) {
                return "int";
            } else if (isStringLiteral(val)) {
                return "string";
            } else if (symbolTable.containsKey(val)) {
                return symbolTable.getType(val);
            }
        }

        String inferredType = "unknown";
        for (int i = 0; i < exprNode.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) exprNode.jjtGetChild(i);
            String childType = analyzeExpression(child);
            if (!childType.equals("unknown") && inferredType.equals("unknown")) {
                inferredType = childType;
            }
        }

        return inferredType;
    }

    private void checkUndeclaredVariables(SimpleNode exprNode) {
        if (exprNode == null) {
            return;
        }

        if (exprNode.value != null && !isLiteral((String) exprNode.value)) {
            String val = (String) exprNode.value;
            if (!symbolTable.containsKey(val)) {
                addError("Variable '" + val + "' is used but not declared");
            }
        }

        for (int i = 0; i < exprNode.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) exprNode.jjtGetChild(i);
            checkUndeclaredVariables(child);
        }
    }

    private boolean isLiteral(String value) {
        if (value == null) {
            return false;
        }
        return isNumericLiteral(value) || isStringLiteral(value);
    }

    private boolean isNumericLiteral(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isStringLiteral(String value) {
        if (value == null) {
            return false;
        }
        return (value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"));
    }

    private void addError(String message) {
        semanticErrors.add("ERROR: " + message);
        errorCount++;
    }

    private void addWarning(String message) {
        semanticWarnings.add("WARNING: " + message);
        warningCount++;
    }

    public List<String> getErrors() {
        return new ArrayList<>(semanticErrors);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(semanticWarnings);
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void printReport() {
        System.out.println("\nSEMANTIC ANALYSIS REPORT");
        System.out.println("=".repeat(70));

        if (errorCount == 0 && warningCount == 0) {
            System.out.println("✓ No semantic errors or warnings found");
        } else {
            if (errorCount > 0) {
                System.out.println("\nERRORS (" + errorCount + "):");
                for (String error : semanticErrors) {
                    System.out.println("  " + error);
                }
            }

            if (warningCount > 0) {
                System.out.println("\nWARNINGS (" + warningCount + "):");
                for (String warning : semanticWarnings) {
                    System.out.println("  " + warning);
                }
            }
        }

    }

    public String getSummary() {
        return String.format("Semantic Analysis: %d errors, %d warnings", errorCount, warningCount);
    }
}
