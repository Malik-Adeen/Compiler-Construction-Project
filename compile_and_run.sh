#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}"
echo "=================================="
echo "CL Compiler - Compile & Run"
echo "=================================="
echo -e "${NC}"

# Step 1: Compile
echo -e "${YELLOW}[1/2] Compiling...${NC}"
bash compile.sh
if [ $? -ne 0 ]; then
    echo -e "${RED}Compilation failed!${NC}"
    exit 1
fi
echo ""

# Step 2: Run
echo -e "${YELLOW}[2/2] Running compiler...${NC}"
echo -e "${NC}"
java -cp generated CLParser
if [ $? -ne 0 ]; then
    echo -e "${RED}Execution failed!${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}=================================="
echo "✓ Execution completed successfully!"
echo "==================================${NC}"
