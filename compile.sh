#!/bin/bash

# CL Compiler Compilation Script

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}"
echo "=================================="
echo "CL Compiler - Compilation"
echo "=================================="
echo -e "${NC}"

# Check if JavaCC is available
if ! command -v jjtree &> /dev/null; then
    echo -e "${RED}Error: jjtree not found in PATH${NC}"
    echo -e "${YELLOW}Please install JavaCC and add it to your PATH${NC}"
    exit 1
fi

if ! command -v javacc &> /dev/null; then
    echo -e "${RED}Error: javacc not found in PATH${NC}"
    echo -e "${YELLOW}Please install JavaCC and add it to your PATH${NC}"
    exit 1
fi

# Create generated directory if it doesn't exist
GENERATED_DIR="generated"
mkdir -p "$GENERATED_DIR"

# Find the grammar file
GRAMMAR_FILE=""
if [ -f "src/CL.jjt" ]; then
    GRAMMAR_FILE="src/CL.jjt"
elif [ -f "CL.jjt" ]; then
    GRAMMAR_FILE="CL.jjt"
else
    echo -e "${RED}Error: CL.jjt not found${NC}"
    exit 1
fi

echo -e "${GREEN}Found grammar file: $GRAMMAR_FILE${NC}"
echo ""

# Step 1: Run JJTree
echo -e "${YELLOW}[1/4] Running JJTree...${NC}"
jjtree "$GRAMMAR_FILE"
if [ $? -ne 0 ]; then
    echo -e "${RED}JJTree failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JJTree completed${NC}"
echo ""

# Step 2: Run JavaCC
echo -e "${YELLOW}[2/4] Running JavaCC...${NC}"
javacc CL.jj
if [ $? -ne 0 ]; then
    echo -e "${RED}JavaCC failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JavaCC completed${NC}"
echo ""

# Step 3: Copy SymbolTable.java to generated folder if it exists
if [ -f "SymbolTable.java" ]; then
    echo -e "${YELLOW}[3/4] Copying SymbolTable.java...${NC}"
    cp SymbolTable.java "$GENERATED_DIR/"
    echo -e "${GREEN}✓ SymbolTable.java copied${NC}"
elif [ -f "src/SymbolTable.java" ]; then
    echo -e "${YELLOW}[3/4] Copying SymbolTable.java...${NC}"
    cp src/SymbolTable.java "$GENERATED_DIR/"
    echo -e "${GREEN}✓ SymbolTable.java copied${NC}"
else
    echo -e "${YELLOW}[3/4] SymbolTable.java not found (skipping)${NC}"
fi
echo ""

# Step 4: Compile all Java files
echo -e "${YELLOW}[4/4] Compiling Java files...${NC}"

# Move all generated .java files to generated directory
mv *.java "$GENERATED_DIR/" 2>/dev/null

# Compile
javac -d "$GENERATED_DIR" "$GENERATED_DIR"/*.java
if [ $? -ne 0 ]; then
    echo -e "${RED}Java compilation failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java compilation completed${NC}"
echo ""

# Cleanup temporary files
echo "Cleaning up temporary files..."
rm -f CL.jj
rm -f *.java 2>/dev/null

echo -e "${GREEN}"
echo "=================================="
echo "✓ Compilation successful!"
echo "=================================="
echo -e "${NC}"
echo "Classes generated in: $GENERATED_DIR"
echo ""