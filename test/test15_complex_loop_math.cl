startProgram
variables:
  int x = 1;
  int z = 0;
code:
  loopif x <= 10 holds
    z = z + x;
    outString("Intermediate Z:");
    outString(z);
    x = x * 2;
  endloop
  
  outString("Final sum of powers of 2:");
  outString(z);
endProgram
