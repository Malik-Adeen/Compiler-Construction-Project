#!/bin/bash

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

# Check JavaCC
if ! command -v jjtree &> /dev/null; then
    echo -e "${RED}Error: jjtree not found${NC}"
    exit 1
fi

if ! command -v javacc &> /dev/null; then
    echo -e "${RED}Error: javacc not found${NC}"
    exit 1
fi

# Create generated directory
mkdir -p generated

# Find grammar file
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

# Step 1: JJTree
echo -e "${YELLOW}[1/5] Running JJTree...${NC}"
jjtree "$GRAMMAR_FILE"
if [ $? -ne 0 ]; then
    echo -e "${RED}JJTree failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JJTree completed${NC}"
echo ""

# Step 2: JavaCC
echo -e "${YELLOW}[2/5] Running JavaCC...${NC}"
javacc CL.jj
if [ $? -ne 0 ]; then
    echo -e "${RED}JavaCC failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JavaCC completed${NC}"
echo ""

# Step 3: Copy custom Java files to root for compilation
echo -e "${YELLOW}[3/5] Preparing custom Java files...${NC}"
if [ -d "src/" ]; then
    cp src/*.java .
    echo -e "${GREEN}✓ Custom Java files prepared from src/${NC}"
else
    echo -e "${GREEN}✓ Custom Java files already in root${NC}"
fi
echo ""

# Step 4: Compile
echo -e "${YELLOW}[4/5] Compiling Java files...${NC}"
javac -d generated *.java
if [ $? -ne 0 ]; then
    echo -e "${RED}Compilation failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Compilation completed${NC}"
echo ""

# Step 5: (Skipped)
echo -e "${YELLOW}[5/5] Organizing files...${NC}"
echo -e "${GREEN}✓ Custom Java files are already tracked in src/${NC}"
echo ""

# Cleanup
echo "Cleaning up..."
rm -f CL.jj
rm -f *.java

echo -e "${GREEN}"
echo "=================================="
echo "✓ Compilation successful!"
echo "=================================="
echo -e "${NC}"
echo "Classes generated in: generated/"
echo ""
echo "To run tests, use: ./run_tests.sh"
echo ""
