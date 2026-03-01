startProgram
variables:
  int outerCount = 5;
  int innerCount = 3;
code:
  loopif outerCount > 0 holds
    outString("Outer Loop!");
    
    // This should fail to parse!
    loopif innerCount > 0 holds
      outString("Failure!");
      innerCount = innerCount - 1;
    endloop

    outerCount = outerCount - 1;
  endloop
endProgram
