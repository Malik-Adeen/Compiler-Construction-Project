startProgram
variables:
  int operation = 2;
  int x = 10;
  int z = 0;
code:
  switchFor (operation)
    case 1:
      outString("Setting Z to 50");
      z = 50;
    case 2:
      outString("Setting Z to X * 20");
      z = x * 20;
    other:
      outString("Operation unknown!");
      z = 0 - 1;
  endswitchFor
  
  outString("Result is:");
  outString(z);
endProgram
