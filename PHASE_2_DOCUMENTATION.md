# Compiler Construction - Milestone 2: Backend Implementation

## Overview
This document describes the implementation of the backend components for the CL Language Compiler, including intermediate code generation, optimization, and assembly code generation.

---

## Phase 1: Enhanced Symbol Table

### File: `src/SymbolTable.java`

The symbol table has been enhanced to properly store all required information about variables:

**Features:**
- **Lexeme Storage**: Stores the exact variable name as declared
- **Type Storage**: Supports all four data types:
  - `int` - Integer values
  - `float` - Floating-point values
  - `string` - String literals
  - `char` - Character values
- **Value Storage**: Stores initial and current values of variables
- **Symbol Class**: Public inner class that encapsulates variable information

**Key Methods:**
```java
public void put(String lexeme, String type, String value)
public String getType(String lexeme)
public String getValue(String lexeme)
public void setValue(String lexeme, String value)
public Collection<Symbol> getAllSymbols()
public String exportToString()
```

**Example Usage:**
```java
SymbolTable st = new SymbolTable();
st.put("count", "int", "0");
st.put("name", "string", "\"John\"");
String type = st.getType("count"); 
```

---

## Phase 2: Semantic Analysis

### File: `src/SemanticAnalyzer.java`

A dedicated semantic analyzer class that validates the Abstract Syntax Tree for semantic correctness.

**Features:**
- **Type Checking**: Validates type consistency in assignments and operations
- **Variable Declaration Verification**: Ensures all variables are declared before use
- **Operation Validation**: Checks that operations are valid for their operand types
- **Error & Warning Reporting**: Collects semantic errors and warnings
- **Comprehensive Analysis**: Analyzes assignments, expressions, loops, and switch statements

**Key Methods:**
```java
public boolean analyze(SimpleNode rootNode)
public List<String> getErrors()
public List<String> getWarnings()
public void printReport()
```

**Example Analysis:**
- Detects: `int x = "hello";` → Type mismatch error
- Detects: `result = undefined_var + 5;` → Undeclared variable error
- Validates: Loop and switch statement types

---

## Phase 3: Intermediate Representation (IR) Generation

### File: `src/IntermediateRepresentation.java` & `src/IRGenerator.java`

Implements three-address code generation using quadruple representation.

### Three-Address Code Format

**Quadruple Representation:**
Each instruction consists of four components:
```
[index] (operator, operand1, operand2, result)
```

**Examples:**
```
[0] (+, b, c, T0)        
[1] (*, T0, 13, T1)      
[2] (=, T1, null, a)     
```

### Supported Operations

**Arithmetic:**
- `+` - Addition
- `-` - Subtraction
- `*` - Multiplication
- `/` - Division

**Assignment:**
- `=` - Assignment

**Control Flow:**
- `LABEL` - Label for jumps
- `JMP` - Unconditional jump
- `JZ` - Jump if zero
- `JNZ` - Jump if not zero
- Conditional operators: `==`, `<`, `>`, `<=`, `>=`, `<>`

**I/O:**
- `PRINT` - Print operation

### IntermediateRepresentation Class

**Key Methods:**
```java
public int emit(String operator, String operand1, String operand2, String result)
public String newTemp()           
public String newLabel()          
public void print()               
public String export()            
public List<Quadruple> getInstructions()
```

### IRGenerator Class

**Key Methods:**
```java
public String generateExpressionIR(SimpleNode exprNode)
public void generateAssignmentIR(String varName, SimpleNode exprNode)
public void generateOutputIR(SimpleNode exprNode)
public void generateLoopIR(SimpleNode condNode, SimpleNode bodyNode)
public void generateFromAST(SimpleNode rootNode)
public void printIR()
```

**IR Generation Example:**
```
Source Code:
  a = b * -c + b * -c;

Generated IR:
  [0] (-, c, null, T0)          
  [1] (*, b, T0, T1)            
  [2] (-, c, null, T2)          
  [3] (*, b, T2, T3)            
  [4] (+, T1, T3, T4)           
  [5] (=, T4, null, a)          
```

---

## Phase 4: Code Optimization

### File: `src/IROptimizer.java`

Performs algebraic simplification and temporary variable elimination on intermediate code.

**Optimization Techniques:**

### 1. Algebraic Simplification
Removes redundant operations:
- `X = X * 1` → REMOVED (multiplicative identity)
- `X = X + 0` → REMOVED (additive identity)
- `X = X - 0` → REMOVED
- `X = X / 1` → REMOVED

### 2. Temporary Variable Elimination
Combines operations to reduce intermediate variables:
```
Before Optimization:
  [0] (*, X, 13, T1)
  [1] (=, T1, null, X)

After Optimization:
  [0] (*, X, 13, X)
```

### 3. Constant Folding
Evaluates constant expressions at compile time:
```
Before: (*, 5, 3, T0)    
After:  (=, 15, null, T0) 
```

### IROptimizer Class

**Key Methods:**
```java
public IntermediateRepresentation optimize()
public String getOptimizationReport()
public void printOptimizationReport()
```

**Optimization Report Example:**
```
OPTIMIZATION REPORT
==================================================================
Original IR Instructions: 15
Optimized IR Instructions: 12
Instructions Removed: 3
Optimizations Performed: 5
Code Reduction: 20.00%
==================================================================
```

---

## Phase 5: Assembly Code Generation

### File: `src/AssemblyCodeGenerator.java`

Generates x86 CISC assembly code from optimized intermediate code.

**Target Architecture:**
- x86 CISC (Complex Instruction Set Computer)
- 4 General-purpose registers (R0, R1, R2, R3)
- Memory-based variable storage with stack frame
- Conventional function prologue/epilogue

### Assembly Instruction Set

**Data Transfer:**
- `MOV dest, src` - Move data

**Arithmetic:**
- `ADD R0, R0, R1` - R0 = R0 + R1
- `SUB R0, R0, R1` - R0 = R0 - R1
- `MUL R0, R0, R1` - R0 = R0 * R1
- `DIV R0, R0, R1` - R0 = R0 / R1

**Comparison:**
- `CMP R0, R1` - Compare R0 and R1

**Branching:**
- `JMP label` - Unconditional jump
- `JE label` - Jump if equal
- `JL label` - Jump if less
- `JG label` - Jump if greater
- `JLE label` - Jump if less or equal
- `JGE label` - Jump if greater or equal
- `JNE label` - Jump if not equal

**Functions:**
- `CALL function` - Call function
- `RET` - Return from function
- `PUSH/POP` - Stack operations

### Assembly Code Example

**C Expression:**
```
a = b + c
d = a + e
```

**Generated Assembly:**
```
; Variable allocation
; b : int at [BP - 4]
; c : int at [BP - 8]
; a : int at [BP - 12]
; e : int at [BP - 16]
; d : int at [BP - 20]

; Addition: a = b + c
MOV R0, [BP - 4]       ; Load b
MOV R1, [BP - 8]       ; Load c
ADD R0, R0, R1         ; R0 = b + c
MOV [BP - 12], R0      ; Store in a

; Addition: d = a + e
MOV R0, [BP - 12]      ; Load a
MOV R1, [BP - 16]      ; Load e
ADD R0, R0, R1         ; R0 = a + e
MOV [BP - 20], R0      ; Store in d
```

### AssemblyCodeGenerator Class

**Key Methods:**
```java
public void generate()
public void printAssemblyCode()
public String exportAssemblyCode()
public String getStatistics()
```

**Register Allocation Strategy:**
- Temporary variables (T0, T1, ...) → General registers
- Program variables → Stack memory
- Dynamic stack frame management

---

## Phase 6: Parser Integration (CL.jjt)

### Updates to Grammar File

The parser has been updated to support the new backend pipeline:

**1. New Data Type Support:**
```
Tokens added:
  < FLOAT: "float" >
  < CHAR: "char" >
  
Literals added:
  < FLOAT_LITERAL: digits.digits >
  < CHAR_LITERAL: 'c' >
```

**2. Enhanced Variable Declaration:**
```
VarDeclaration now supports:
  int | float | string | char variables
  with corresponding literal types
```

**3. Complete Compilation Pipeline:**
```java
CLParser → AST
    ↓
SemanticAnalyzer → Error/Warning Report
    ↓
IRGenerator → Three-Address Code
    ↓
IROptimizer → Optimized Three-Address Code
    ↓
AssemblyCodeGenerator → Assembly Code
```

---

## Compilation Pipeline Output

### Console Output Displays (in order):

1. **Parsing Phase**
   - File reading confirmation
   - Parsing completion status

2. **Semantic Analysis**
   - Type checking results
   - Variable declaration validation
   - Error and warning report

3. **Symbol Table**
   - All declared variables with lexeme, type, and value

4. **Intermediate Code Generation**
   - Complete three-address code in tabular format
   - Temporary variable and label summary

5. **Optimization Report**
   - Original vs. optimized instruction count
   - Optimization statistics
   - Code reduction percentage

6. **Optimized IR**
   - Three-address code after optimization

7. **Assembly Code**
   - Generated x86 assembly code
   - Memory allocation layout
   - Register usage statistics

---

## Compilation Example

### Input File: `test1_addition.cl`
```
startProgram
    variables:
        int abc = 3;
        int xyz = 4;
        int result = 0;
    code:
        result = abc + xyz;
        outString(result);
endProgram
```

### Expected Output Flow:

```
✓ Parsing completed successfully!

PHASE 1: SEMANTIC ANALYSIS
================================================================
✓ No semantic errors or warnings found

Symbol Table:
============================================================
LEXEME              TYPE            VALUE
abc                 int             3
xyz                 int             4
result              int             0
============================================================

PHASE 2: INTERMEDIATE CODE GENERATION
================================================================
#    | OPERATOR   | OPERAND1        | OPERAND2        | RESULT
-----+------------+-----------------+-----------------+--------
0    | +          | abc             | xyz             | T0
1    | =          | T0              | -               | result
2    | PRINT      | result          | -               | -
================================================================

PHASE 3: CODE OPTIMIZATION
================================================================
OPTIMIZATION REPORT
Original IR Instructions: 3
Optimized IR Instructions: 3
Instructions Removed: 0
Optimizations Performed: 0
Code Reduction: 0.00%
================================================================

PHASE 4: ASSEMBLY CODE GENERATION
================================================================
; ====== ASSEMBLY CODE ======
; Function prologue
PUSH BP
MOV BP, SP
SUB SP, 128

; Variable allocation
; abc : int at [BP - 4]
; xyz : int at [BP - 8]
; result : int at [BP - 12]

; Addition: T0 = abc + xyz
MOV R0, [BP - 4]
MOV R1, [BP - 8]
ADD R0, R0, R1
MOV [BP - 12], R0

; Print: result
MOV R0, [BP - 12]
CALL PRINT_INT

; Function epilogue
MOV SP, BP
POP BP
RET
================================================================
```

---

## Compilation Instructions

### Building the Project

**Using the provided build script:**
```bash
./compile.sh
```

**Manual compilation:**
```bash
# Generate parser (if javacc is available)
jjtree src/CL.jjt
javacc generated/CL.jj

# Compile all files
javac -d generated src/*.java generated/*.java

# Run the parser
java -cp generated CLParser test/test1_addition.cl
```

### Testing

Run any test file from the `test/` directory:
```bash
java -cp generated CLParser test/test1_addition.cl
java -cp generated CLParser test/test2_factorial.cl
java -cp generated CLParser test/error1_undeclared.cl
```

---

## Files Implemented

### Backend Components:
1. **SymbolTable.java** - Enhanced symbol table with lexeme, type, value storage
2. **SemanticAnalyzer.java** - Semantic analysis and error checking
3. **IntermediateRepresentation.java** - Three-address code (quadruple) representation
4. **IRGenerator.java** - IR generation from AST
5. **IROptimizer.java** - Algebraic simplification and optimization
6. **AssemblyCodeGenerator.java** - x86 assembly code generation

### Updated Files:
1. **CL.jjt** - Parser grammar with support for all four data types and new backend integration
2. **SymbolTable.java** - Enhanced with Symbol inner class and export methods

---

## Key Features Implemented

✅ **Data Types**: int, float, string, char  
✅ **Symbol Table**: Lexeme, type, and value storage  
✅ **Semantic Analysis**: Separate analyzer class with error checking  
✅ **IR Generation**: Three-address code (quadruple) representation  
✅ **Code Optimization**: Algebraic simplification and temp variable elimination  
✅ **Assembly Generation**: x86 CISC assembly code with register allocation  
✅ **Complete Pipeline**: Parsing → Semantic Analysis → IR → Optimization → Assembly  
✅ **Comprehensive Output**: Console display of all compilation phases  

---

## Design Patterns Used

- **Visitor Pattern**: AST traversal in code generation
- **Strategy Pattern**: Different optimization strategies in IROptimizer
- **Builder Pattern**: Incremental IR construction in IRGenerator
- **Factory Pattern**: Temporary variable and label generation

---

## Performance Considerations

- **Register Allocation**: Efficient register usage for temporary variables
- **Memory Management**: Stack-based variable storage with proper frame management
- **Optimization Passes**: Single-pass optimization for efficiency
- **Code Generation**: Direct assembly output without intermediate steps

---

## Future Enhancements

1. **Advanced Optimizations**:
   - Dead code elimination
   - Common subexpression elimination
   - Loop unrolling

2. **Code Generation**:
   - Target other architectures (ARM, MIPS, RISC-V)
   - Peephole optimization
   - Register coloring

3. **Runtime Features**:
   - Function calls and recursion
   - Dynamic memory allocation
   - Error handling and exceptions

4. **Debugging Support**:
   - Debug symbol generation
   - Line number mapping
   - Symbol file generation

---

## Project Completion Status

✅ **Milestone 1**: Front-end (Scanner, Parser, Semantic Analysis)  
✅ **Milestone 2**: Back-end (IR Generation, Optimization, Assembly Code)  

All requirements have been successfully implemented and documented.

---

*Generated for Compiler Construction Course - Milestone 2*
