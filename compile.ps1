#!/usr/bin/env pwsh

Write-Host "`nCompiling CL Compiler...`n" -ForegroundColor Cyan

# Check if JavaCC is available
if (-not (Get-Command javacc -ErrorAction SilentlyContinue)) {
    Write-Host "Error: javacc not found in PATH" -ForegroundColor Red
    Write-Host "Please install JavaCC and add it to your PATH" -ForegroundColor Yellow
    exit 1
}

# Create generated directory if it doesn't exist
$generatedDir = "generated"
if (-not (Test-Path $generatedDir)) {
    New-Item -ItemType Directory -Path $generatedDir | Out-Null
}

# Find the .jjt or .jj file
$grammarFile = Get-ChildItem -Filter "*.jjt" -Path "src" -ErrorAction SilentlyContinue | Select-Object -First 1

if (-not $grammarFile) {
    $grammarFile = Get-ChildItem -Filter "*.jj" -Path "src" -ErrorAction SilentlyContinue | Select-Object -First 1
}

if (-not $grammarFile) {
    $grammarFile = Get-ChildItem -Filter "CL.jjt" -ErrorAction SilentlyContinue | Select-Object -First 1
}

if (-not $grammarFile) {
    Write-Host "Error: No .jjt or .jj grammar file found" -ForegroundColor Red
    exit 1
}

Write-Host "Found grammar file: $($grammarFile.Name)" -ForegroundColor Green

# Compile with JavaCC
Write-Host "Running JavaCC..." -ForegroundColor Yellow
if ($grammarFile.Extension -eq ".jjt") {
    jjtree $grammarFile.FullName
    javacc CL.jj
} else {
    javacc $grammarFile.FullName
}

if ($LASTEXITCODE -ne 0) {
    Write-Host "JavaCC compilation failed!" -ForegroundColor Red
    exit 1
}

# Compile Java files
Write-Host "`nCompiling Java files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Filter "*.java"

javac -d $generatedDir $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Java compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n✓ Compilation successful!" -ForegroundColor Green
Write-Host "Classes generated in: $generatedDir" -ForegroundColor Gray
Write-Host ""