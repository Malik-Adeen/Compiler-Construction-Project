startProgram
    variables:
        int num = 1;
        int val = 0;
    code:
        switchFor (num)
            case 1 : val = 100;
            case 2 : val = 200;
        endswitchFor
        outString(val);
endProgram