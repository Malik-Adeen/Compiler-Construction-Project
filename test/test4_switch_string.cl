startProgram
    variables:
        string grade = "A";
        int score = 0;
    code:
        switchFor (grade)
            case "A" : score = 95;
            case "B" : score = 85;
            case "C" : score = 75;
            other : score = 50;
        endswitchFor
        outString(score);
endProgram