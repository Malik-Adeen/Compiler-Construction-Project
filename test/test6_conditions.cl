startProgram
    variables:
        int x = 10;
        int y = 5;
        int result = 0;
    code:
        loopif x > y holds
            result = 1;
        endloop
        loopif x >= 10 holds
            result = 2;
        endloop
        loopif y < x holds
            result = 3;
        endloop
        loopif y <= 5 holds
            result = 4;
        endloop
        loopif x == 10 holds
            result = 5;
        endloop
        loopif x <> y holds
            result = 6;
        endloop
        outString(result);
endProgram