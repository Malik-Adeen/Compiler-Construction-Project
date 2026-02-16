#!/usr/bin/env pwsh
# CL Compiler Test Suite - PowerShell Version
# Place this in the root directory (same level as 'generated' and 'test' folders)

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "CL Compiler Test Suite" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Counter
$pass = 0
$fail = 0
$total = 0

# Change to the generated directory where the compiled classes are
$generatedDir = Join-Path $PSScriptRoot "generated"
$testDir = Join-Path $PSScriptRoot "test"

# Check if directories exist
if (-not (Test-Path $generatedDir)) {
    Write-Host "Error: 'generated' directory not found!" -ForegroundColor Red
    Write-Host "Please compile your parser first with: javacc CL.jjt && javac *.java" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path $testDir)) {
    Write-Host "Error: 'test' directory not found!" -ForegroundColor Red
    exit 1
}

# Set classpath to include the generated directory
$env:CLASSPATH = $generatedDir

Write-Host "Using generated classes from: $generatedDir" -ForegroundColor Gray
Write-Host "Using test files from: $testDir" -ForegroundColor Gray
Write-Host ""

# Test valid programs
Write-Host "--- VALID PROGRAMS ---" -ForegroundColor Yellow
Write-Host ""

$validTests = Get-ChildItem -Path $testDir -Filter "test*.cl" | Sort-Object Name

foreach ($testFile in $validTests) {
    $total++
    Write-Host "Testing $($testFile.Name) ... " -NoNewline
    
    try {
        # Run the parser with the test file
        $output = & java -cp $generatedDir CLParser $testFile.FullName 2>&1 | Out-String
        
        if ($LASTEXITCODE -eq 0 -or $output -match "Parsing completed successfully") {
            if ($output -match "Parsing completed successfully") {
                Write-Host "✓ PASS" -ForegroundColor Green
                $pass++
            } else {
                Write-Host "✗ FAIL (No success message)" -ForegroundColor Red
                $fail++
                Write-Host $output -ForegroundColor DarkGray
            }
        } else {
            Write-Host "✗ FAIL" -ForegroundColor Red
            $fail++
            Write-Host $output -ForegroundColor DarkGray
        }
    }
    catch {
        Write-Host "✗ FAIL (Exception)" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor DarkGray
        $fail++
    }
    Write-Host ""
}

# Test error programs
Write-Host "--- ERROR PROGRAMS (Should Fail) ---" -ForegroundColor Yellow
Write-Host ""

$errorTests = Get-ChildItem -Path $testDir -Filter "error*.cl" | Sort-Object Name

foreach ($testFile in $errorTests) {
    $total++
    Write-Host "Testing $($testFile.Name) ... " -NoNewline
    
    try {
        $output = & java -cp $generatedDir CLParser $testFile.FullName 2>&1 | Out-String
        
        if ($LASTEXITCODE -eq 0 -and $output -match "Parsing completed successfully") {
            Write-Host "✗ FAIL (Should have failed but passed)" -ForegroundColor Red
            $fail++
        } else {
            if ($output -match "Error|error|Exception") {
                Write-Host "✓ PASS (Correctly caught error)" -ForegroundColor Green
                $pass++
            } else {
                Write-Host "⚠ WARN (Failed but no error message)" -ForegroundColor Yellow
                $fail++
            }
        }
        
        # Show first 3 lines of error output
        $lines = ($output -split "`n" | Where-Object { $_.Trim() -ne "" } | Select-Object -First 3)
        foreach ($line in $lines) {
            Write-Host "  $line" -ForegroundColor DarkGray
        }
    }
    catch {
        Write-Host "✓ PASS (Correctly caught error)" -ForegroundColor Green
        Write-Host "  $($_.Exception.Message)" -ForegroundColor DarkGray
        $pass++
    }
    Write-Host ""
}

# Summary
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Test Results:" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Total Tests:  $total" -ForegroundColor White
Write-Host "Passed:       $pass" -ForegroundColor Green
Write-Host "Failed:       $fail" -ForegroundColor Red

if ($fail -eq 0) {
    Write-Host ""
    Write-Host "🎉 All tests passed!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "⚠️  Some tests failed. Review output above." -ForegroundColor Yellow
}

Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Calculate percentage
if ($total -gt 0) {
    $percentage = [math]::Round(($pass / $total) * 100, 2)
    Write-Host "Success Rate: $percentage%" -ForegroundColor $(if ($percentage -eq 100) { "Green" } else { "Yellow" })
}

Write-Host ""