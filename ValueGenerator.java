/*
Kristen Niekamp
Lab 2 - Hashing

This is an additional program to generate files with values for testing
purposes. The desired number of values is input and a text file containing
a random list of values between 0 and 99,999 is output.

Usage: java ValueGenerator [output.txt] (int)[numOfValues]
*/

import java.io.*;
import java.util.*;

class ValueGenerator {
  public static void main(String[] args) throws IOException {

    FileWriter outputStream = new FileWriter(args[0],true); //setup to write output file
    BufferedWriter output = new BufferedWriter(outputStream);

    if(args.length == 0) {
      System.out.println("Usage: java ValueGenerator outputFile.txt numOfValues");
      System.out.println("Values will come out within the range 0-99,999");
    }

    int numOfValues = Integer.parseInt(args[1]);
    int min = 0;
    int max = 99999;
    int range = max - min + 1; //values will be within range 0-99,999
    double val;

    for (int n = 0; n < numOfValues; n++) {
      val = Math.random() * range + min;//creates random values
      output.write((int)val + "\n");
      // output.write(" " + n + "\n"); //this line used to print value 1...n in order
    }

    try{
      output.close();
    } catch (IOException e) {
      System.out.println("There was an error closing the output file: " + e.getMessage());
    }
  }
}
