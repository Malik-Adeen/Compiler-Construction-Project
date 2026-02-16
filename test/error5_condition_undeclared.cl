startProgram
    variables:
        int x = 5;
    code:
        loopif x > undeclaredVar holds
            x = x - 1;
        endloop
        outString(x);
endProgram