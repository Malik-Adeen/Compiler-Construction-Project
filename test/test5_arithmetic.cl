startProgram
    variables:
        int a = 10;
        int b = 5;
        int c = 2;
        int result = 0;
    code:
        result = a + b * c;
        outString(result);
        result = a - b / c;
        outString(result);
        result = (a + b) * c;
        outString(result);
endProgram