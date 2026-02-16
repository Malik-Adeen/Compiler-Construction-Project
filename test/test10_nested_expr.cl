startProgram
    variables:
        int a = 5;
        int b = 3;
        int c = 2;
        int result = 0;
    code:
        result = ((a + b) * c) - (a / c);
        outString(result);
endProgram