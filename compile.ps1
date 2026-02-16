#!/usr/bin/env pwsh
# CL Compiler Compilation Script for Windows

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "CL Compiler - Compilation" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check if JavaCC is available
if (-not (Get-Command jjtree -ErrorAction SilentlyContinue)) {
    Write-Host "Error: jjtree not found in PATH" -ForegroundColor Red
    Write-Host "Please install JavaCC and add it to your PATH" -ForegroundColor Yellow
    exit 1
}

if (-not (Get-Command javacc -ErrorAction SilentlyContinue)) {
    Write-Host "Error: javacc not found in PATH" -ForegroundColor Red
    Write-Host "Please install JavaCC and add it to your PATH" -ForegroundColor Yellow
    exit 1
}

# Create generated directory if it doesn't exist
$generatedDir = "generated"
if (-not (Test-Path $generatedDir)) {
    New-Item -ItemType Directory -Path $generatedDir | Out-Null
    Write-Host "Created directory: $generatedDir" -ForegroundColor Gray
}

# Find the grammar file
$grammarFile = $null
if (Test-Path "src/CL.jjt") {
    $grammarFile = "src/CL.jjt"
} elseif (Test-Path "CL.jjt") {
    $grammarFile = "CL.jjt"
} else {
    Write-Host "Error: CL.jjt not found" -ForegroundColor Red
    exit 1
}

Write-Host "Found grammar file: $grammarFile" -ForegroundColor Green
Write-Host ""

# Step 1: Run JJTree
Write-Host "[1/5] Running JJTree..." -ForegroundColor Yellow
jjtree $grammarFile
if ($LASTEXITCODE -ne 0) {
    Write-Host "JJTree failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ JJTree completed" -ForegroundColor Green
Write-Host ""

# Step 2: Run JavaCC
Write-Host "[2/5] Running JavaCC..." -ForegroundColor Yellow
javacc CL.jj
if ($LASTEXITCODE -ne 0) {
    Write-Host "JavaCC failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ JavaCC completed" -ForegroundColor Green
Write-Host ""

# Step 3: Copy SymbolTable.java to current directory (needed for compilation)
Write-Host "[3/5] Preparing SymbolTable.java..." -ForegroundColor Yellow
$symbolTableSource = $null
if (Test-Path "src/SymbolTable.java") {
    $symbolTableSource = "src/SymbolTable.java"
} elseif (Test-Path "SymbolTable.java") {
    $symbolTableSource = "SymbolTable.java"
} else {
    Write-Host "Error: SymbolTable.java not found" -ForegroundColor Red
    exit 1
}

# Copy to current directory for compilation
Copy-Item $symbolTableSource -Destination "." -Force
Write-Host "✓ SymbolTable.java prepared" -ForegroundColor Green
Write-Host ""

# Step 4: Compile all Java files
Write-Host "[4/5] Compiling Java files..." -ForegroundColor Yellow

# Get all Java files in current directory
$javaFiles = Get-ChildItem -Filter "*.java" -File

if ($javaFiles.Count -eq 0) {
    Write-Host "Error: No Java files found to compile" -ForegroundColor Red
    exit 1
}

Write-Host "Compiling $($javaFiles.Count) Java files..." -ForegroundColor Gray

# Compile all Java files and output to generated directory
javac -d $generatedDir *.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "Java compilation failed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Common issues:" -ForegroundColor Yellow
    Write-Host "  1. Make sure SymbolTable.java is in src/ directory" -ForegroundColor Gray
    Write-Host "  2. Check that CLParser references symbolTable correctly" -ForegroundColor Gray
    Write-Host "  3. Verify Java version compatibility" -ForegroundColor Gray
    exit 1
}
Write-Host "✓ Java compilation completed" -ForegroundColor Green
Write-Host ""

# Step 5: Copy SymbolTable.java to generated folder for Git tracking
Write-Host "[5/5] Organizing files..." -ForegroundColor Yellow
Copy-Item "SymbolTable.java" -Destination $generatedDir -Force
Write-Host "✓ SymbolTable.java copied to $generatedDir" -ForegroundColor Green

# Cleanup temporary files
Write-Host ""
Write-Host "Cleaning up temporary files..." -ForegroundColor Gray
Remove-Item "CL.jj" -ErrorAction SilentlyContinue

# Remove Java source files from root (keep only in generated/)
$filesToRemove = @(
    "*.java"
)

foreach ($file in $filesToRemove) {
    Remove-Item $file -ErrorAction SilentlyContinue
}

Write-Host "✓ Cleanup completed" -ForegroundColor Green
Write-Host ""

Write-Host "==================================" -ForegroundColor Green
Write-Host "✓ Compilation successful!" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green
Write-Host ""
Write-Host "Classes generated in: $generatedDir" -ForegroundColor Gray
Write-Host ""
Write-Host "To run tests, use:" -ForegroundColor Cyan
Write-Host "  .\run_tests.ps1" -ForegroundColor White
Write-Host ""