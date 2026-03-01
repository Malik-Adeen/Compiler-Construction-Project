startProgram
variables:
  int counter = 5;
code:
  loopif counter > 0 holds
    outString("Counter is currently at:");
    outString(counter);
    counter = counter - 1;
  endloop
  outString("Loop finished!");
endProgram
