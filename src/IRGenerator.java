
public class IRGenerator {

    private IntermediateRepresentation ir;
    private SymbolTable symbolTable;

    public IRGenerator(SymbolTable symbolTable) {
        this.ir = new IntermediateRepresentation();
        this.symbolTable = symbolTable;
    }

    public String generateExpressionIR(SimpleNode exprNode) {
        if (exprNode == null) {
            return null;
        }

        int numChildren = exprNode.jjtGetNumChildren();

        if (numChildren == 0) {
            String value = (String) exprNode.value;
            if (value != null) {
                return value;
            }
            return null;
        }

        if (numChildren == 2) {
            SimpleNode child1 = (SimpleNode) exprNode.jjtGetChild(0);
            SimpleNode child2 = (SimpleNode) exprNode.jjtGetChild(1);

            String operand1 = generateExpressionIR(child1);
            String operand2 = generateExpressionIR(child2);

            String operator = getOperator(exprNode);

            String result = ir.newTemp();
            ir.emit(operator, operand1, operand2, result);
            return result;
        }

        if (numChildren == 1) {
            return generateExpressionIR((SimpleNode) exprNode.jjtGetChild(0));
        }

        return null;
    }

    public void generateAssignmentIR(Object varNameObj, SimpleNode exprNode) {
        String varName = (String) varNameObj;
        String exprResult = generateExpressionIR(exprNode);
        if (exprResult != null) {
            ir.emit("=", exprResult, null, varName);
        }
    }

    public void generateOutputIR(SimpleNode exprNode) {
        String exprResult = generateExpressionIR(exprNode);
        if (exprResult != null) {
            ir.emit("PRINT", exprResult, null, null);
        }
    }

    public String generateConditionIR(SimpleNode conditionNode, String trueLabel, String falseLabel) {

        if (conditionNode == null || conditionNode.jjtGetNumChildren() < 2) {
            return null;
        }

        SimpleNode leftChild = (SimpleNode) conditionNode.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) conditionNode.jjtGetChild(1);

        String left = generateExpressionIR(leftChild);
        String right = generateExpressionIR(rightChild);

        String operator = "==";

        ir.emit(operator, left, right, trueLabel);
        return falseLabel;
    }

    public void generateLoopIR(SimpleNode conditionNode, SimpleNode loopNode) {
        String startLabel = ir.newLabel();
        String endLabel = ir.newLabel();

        ir.emit("LABEL", startLabel, null, null);

        generateConditionIR(conditionNode, null, endLabel);

        if (loopNode != null && loopNode.jjtGetNumChildren() > 1) {
            for (int i = 1; i < loopNode.jjtGetNumChildren(); i++) {
                SimpleNode stmt = (SimpleNode) loopNode.jjtGetChild(i);
                generateStatementIR(stmt);
            }
        }

        ir.emit("JMP", startLabel, null, null);
        ir.emit("LABEL", endLabel, null, null);
    }

    public void generateSwitchIR(String switchVar, SimpleNode caseNode) {
        String endLabel = ir.newLabel();

        if (caseNode != null) {
            for (int i = 0; i < caseNode.jjtGetNumChildren(); i++) {
                SimpleNode caseClause = (SimpleNode) caseNode.jjtGetChild(i);
                String caseLabel = ir.newLabel();

                ir.emit("LABEL", caseLabel, null, null);
            }
        }

        ir.emit("LABEL", endLabel, null, null);
    }

    public void generateStatementIR(SimpleNode stmtNode) {
        if (stmtNode == null) {
            return;
        }

        int numChildren = stmtNode.jjtGetNumChildren();

        if (stmtNode.value != null && numChildren == 1) {

            SimpleNode exprNode = (SimpleNode) stmtNode.jjtGetChild(0);
            generateAssignmentIR((String) stmtNode.value, exprNode);
        } else if (stmtNode.value == null && numChildren == 1) {

            SimpleNode exprNode = (SimpleNode) stmtNode.jjtGetChild(0);
            generateOutputIR(exprNode);
        } else if (stmtNode.value == null && numChildren >= 2) {

            SimpleNode condNode = (SimpleNode) stmtNode.jjtGetChild(0);
            generateLoopIR(condNode, stmtNode);
        } else if (stmtNode.value != null && numChildren >= 2) {

            SimpleNode caseNode = (SimpleNode) stmtNode.jjtGetChild(0);
            generateSwitchIR((String) stmtNode.value, caseNode);
        }
    }

    public void generateFromAST(SimpleNode rootNode) {
        if (rootNode == null) {
            return;
        }

        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) rootNode.jjtGetChild(i);
            generateStatementIR(child);
        }
    }

    private String getOperator(SimpleNode node) {
        if (node.value != null) {
            return (String) node.value;
        }

        String nodeName = node.toString();
        if (nodeName.contains("Add"))
            return "+";
        if (nodeName.contains("Sub"))
            return "-";
        if (nodeName.contains("Mul"))
            return "*";
        if (nodeName.contains("Div"))
            return "/";
        return "?";
    }

    public IntermediateRepresentation getIR() {
        return ir;
    }

    public void printIR() {
        ir.print();
    }
}
