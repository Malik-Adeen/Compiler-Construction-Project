startProgram
    variables:
        int day = 3;
        int hours = 0;
    code:
        switchFor (day)
            case 1 : hours = 8;
            case 2 : hours = 6;
            case 3 : hours = 10;
            other : hours = 0;
        endswitchFor
        outString(hours);
endProgram