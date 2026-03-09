#!/bin/bash

# CL Compiler Test Suite - Bash Script for Linux

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
GRAY='\033[0;90m'
NC='\033[0m' # No Color

# Counter
pass=0
fail=0
total=0

echo -e "${CYAN}==================================${NC}"
echo -e "${CYAN}CL Compiler Test Suite${NC}"
echo -e "${CYAN}==================================${NC}"
echo ""

# Directory setup
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
GENERATED_DIR="$SCRIPT_DIR/generated"
TEST_DIR="$SCRIPT_DIR/test"

# Check if directories exist
if [ ! -d "$GENERATED_DIR" ]; then
    echo -e "${RED}Error: 'generated' directory not found!${NC}"
    echo -e "${YELLOW}Please compile your parser first with: ./compile.sh${NC}"
    exit 1
fi

if [ ! -d "$TEST_DIR" ]; then
    echo -e "${RED}Error: 'test' directory not found!${NC}"
    exit 1
fi

echo -e "${GRAY}Using generated classes from: $GENERATED_DIR${NC}"
echo -e "${GRAY}Using test files from: $TEST_DIR${NC}"
echo ""

# Test valid programs
echo -e "${YELLOW}--- VALID PROGRAMS ---${NC}"
echo ""

for testfile in "$TEST_DIR"/test*.cl; do
    if [ -f "$testfile" ]; then
        ((total++))
        filename=$(basename "$testfile")
        echo -ne "Testing ${filename} ... "
        
        output=$(java -cp "$GENERATED_DIR" CLParser "$testfile" 2>&1)
        exit_code=$?
        
        if echo "$output" | grep -q "Parsing completed successfully"; then
            echo -e "${GREEN}✓ PASS${NC}"
            ((pass++))
        else
            echo -e "${RED}✗ FAIL${NC}"
            ((fail++))
            echo "$output"
        fi
    fi
done

echo ""

# Test error programs
echo -e "${YELLOW}--- ERROR PROGRAMS (Should Fail) ---${NC}"
echo ""

for testfile in "$TEST_DIR"/error*.cl; do
    if [ -f "$testfile" ]; then
        filename=$(basename "$testfile")
        
        ((total++))
        echo -ne "Testing ${filename} ... "
        
        output=$(java -cp "$GENERATED_DIR" CLParser "$testfile" 2>&1)
        exit_code=$?
        
        if echo "$output" | grep -q "Parsing completed successfully"; then
            echo -e "${RED}✗ FAIL (Should have failed but passed)${NC}"
            ((fail++))
        else
            if echo "$output" | grep -q -i "error"; then
                echo -e "${GREEN}✓ PASS (Correctly caught error)${NC}"
                ((pass++))
            else
                echo -e "${YELLOW}⚠ WARN${NC}"
                ((fail++))
            fi
        fi
        
        # Show first 2 lines of error (without duplicates)
        echo "$output" | grep -i "error" | head -1 | sed 's/^/  /'
    fi
done

echo ""

# Summary
echo -e "${CYAN}==================================${NC}"
echo -e "${CYAN}Test Results:${NC}"
echo -e "${CYAN}==================================${NC}"
echo "Total Tests:  $total"
echo -e "${GREEN}Passed:       $pass${NC}"
echo -e "${RED}Failed:       $fail${NC}"

if [ $fail -eq 0 ]; then
    echo ""
    echo -e "${GREEN}🎉 All tests passed!${NC}"
else
    echo ""
    echo -e "${YELLOW}⚠️  Some tests failed. Review output above.${NC}"
fi

echo -e "${CYAN}==================================${NC}"

if [ $total -gt 0 ]; then
    percentage=$(awk "BEGIN {printf \"%.2f\", ($pass / $total) * 100}")
    if [ "$percentage" = "100.00" ]; then
        echo -e "${GREEN}Success Rate: $percentage%${NC}"
    else
        echo -e "${YELLOW}Success Rate: $percentage%${NC}"
    fi
fi

echo ""