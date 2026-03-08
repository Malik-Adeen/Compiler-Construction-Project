# CL Language Compiler

Compiler front-end for the Classroom Language (CL) - A simple educational programming language.

## Project Structure
```
├── src/               # Source files
│   ├── CL.jjt        # JavaCC grammar
│   └── SymbolTable.java
├── generated/         # Compiled output
├── test/             # Test cases
├── compile.sh        # Build script (Linux/Mac)
├── compile.ps1       # Build script (Windows)
├── run_tests.sh      # Test script (Linux/Mac)
└── run_tests.ps1     # Test script (Windows)
```

## Prerequisites

- Java JDK 8 or higher
- JavaCC (Java Compiler Compiler)

### Installing JavaCC

**Linux/Mac:**
```bash
sudo apt install javacc  # Ubuntu/Debian
brew install javacc      # macOS
```

**Windows:**
Download from: https://javacc.github.io/javacc/

## Building

**Linux/Mac:**
```bash
chmod +x compile.sh
./compile.sh
```

**Windows:**
```powershell
.\compile.ps1
```

## Running Tests

**Linux/Mac:**
```bash
chmod +x run_tests.sh
./run_tests.sh
```

**Windows:**
```powershell
.\run_tests.ps1
```

## Testing Individual Files
```bash
# Linux/Mac
java -cp generated CLParser test/test1_addition.cl

# Windows
java -cp generated CLParser test\test1_addition.cl
```

## Language Features

- **Data Types:** int, string
- **Statements:** Assignment, loopif, switchFor, outString
- **Operators:** +, -, *, / (arithmetic), >, <, >=, <=, ==, <> (conditional)

## Example Program
```cl
startProgram
    variables:
        int num = 5;
        int fact = 1;
    code:
        loopif num > 0 holds
            fact = fact * num;
            num = num - 1;
        endloop
        outString(fact);
endProgram
```

## Semantic Analysis

The compiler performs:
- ✅ Variable declaration checking
- ✅ Duplicate declaration detection
- ✅ Variable usage validation
- ✅ Type checking for assignments and expressions (arithmetic operations)
- ✅ Type checking for switchFor variables

## Contributors

[Your Name(s) and IDs]

## Course Information

- **Course:** Compiler Construction
- **Semester:** Spring 2026
- **Instructor:** Dr. Sabina Akhtar