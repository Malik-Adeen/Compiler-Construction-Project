startProgram
variables:
  string greeting = "Initial String";
  string newStr = "A different string";
code:
  outString(greeting);
  greeting = newStr;
  outString(greeting);
  greeting = "Brand new string literal assigned in code!";
  outString(greeting);
endProgram
