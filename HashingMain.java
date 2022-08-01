/*
Kristen Niekamp
Lab 2 - Hashing

This class contains the main driver for the Hashing lab. It handles input/output
and sending input data to respective classes for analysis.
*/

import java.util.*;
import java.io.*;
import java.lang.Math;

// Command line: java HashingMain inputFile.txt (int)divisor (int)bucketSize

class HashingMain {

/*
This is the main driver
It does initial I/O error checking and passes the input
array to the hashing function
*/
  public static void main(String[] args) throws IOException {
    HashingMain hm = new HashingMain();

    if (args.length != 4) {
      System.out.println("Usage: java HashingMain inputfile.txt outputfile.txt (int)divisor (int)bucketSize");
      System.exit(0);
    }

    int divisor = Integer.parseInt(args[2]);//extract divisor and bucket size from command line
    int bucketSize = Integer.parseInt(args[3]);

    //command line error checking
    if (divisor < 0 || divisor > 120) {
      System.out.println("For Midsquare hashing, enter '0' for the divisor. For modulo division hashing, enter a number between 0 and 120.");
      System.exit(0);
    }
    else if (bucketSize < 1 || 120 % bucketSize != 0) {
      System.out.println("Bucket size must be greater than 0 and a factor of 120.");
      System.exit(0);
    }

    File inputFile = null;
    Scanner scan = null;//scanners to read input
    Scanner countLines = null;
    FileWriter outputStream = new FileWriter(args[1],true); //setup to write output file
    BufferedWriter output = new BufferedWriter(outputStream);
    int[] inputVals;
    int[] extraVals;


    try {
      inputFile = new File(args[0]);
      scan = new Scanner(inputFile);
      countLines = new Scanner(inputFile);
    } catch (IOException ioe) {
      System.out.println("There was an error regarding the input file: " + ioe.getMessage());
    }

    //check that the input file is not empty
    if (inputFile.length() == 0) {
      System.out.println("The input file is empty. Please provide values for hashing.");
      output.write("The input file is empty. Please provide values for hashing.");
      scan.close();
      output.close();
      System.exit(0);
    }

    int lines = hm.countLines(countLines);//will be used to make input values array
    inputVals = hm.readInput(lines, scan, output);

    //check that input file contained numbers to be hashed
    //letters and spaces are not inserted into input array
    if (inputVals[0] == -1) {
      System.out.println("Please provide values to be hashed.");
      output.write("Please provide values to be hashed.");
      scan.close();
      output.close();
      System.exit(0);
    }

    //input values are sent to be hashed after error checking
    hm.Hash(inputVals, divisor, bucketSize, output);

    output.close();
    scan.close();
  }//end main

/*
This method reads the input one line at a time.
Words, spaces, and empty lines are ignored. Lines with letters and numbers are ignored.
Only integer values are inserted into input array.
*/
  public int[] readInput(int numLines, Scanner scan, BufferedWriter output) throws IOException {
    int[] inputVals = new int[numLines+1];//+1 allows -1 flag at end to mark end of input values

    //initialize input array to -1
    for (int i = 0; i < inputVals.length; i++) {
      inputVals[i] = -1;
    }

    int m = 0; //keep track of current index of the input array
    String currLine;

    while (scan.hasNext()) {
      currLine = scan.nextLine();
      currLine = currLine.replaceAll(" ", "");//remove spaces in lines
      for (int i = 0; i < currLine.length(); i++) {
        //if a line contains characters other than numbers, the line is marked as empty and ignored
        if (currLine.charAt(i) < '0' || currLine.charAt(i) > '9') {
          currLine = "";
        }
      }
      if (!currLine.equals("")) {
        inputVals[m] = Integer.parseInt(currLine);
      }
      else {//start next iteration of loop without increasing m
        continue;
      }
      m++;
    }

    return inputVals;
  }

  //count number of lines to be used in creating future arrays
  public int countLines (Scanner readFile) {
    int numLines = 0;
    while (readFile.hasNextLine()) {
      numLines++;
      readFile.nextLine();
    }
    return numLines;
  }

/*
This is the main method for handling input values, hash locations,
and sending those values to the proper corresponding collision handling
method.
Each collision handling technique has its own hash table. Linear and Quadratic
Probing use 2-dimensional int arrays to be able to handle buckets larger than 1.
Chaining within the table uses a Node array with attributes for index, bucket,
and next pointers.
The hash function depends on the input divisor from the command line.
*/
  public void Hash(int[] inputVals, int divisor, int bucketSize, BufferedWriter output) throws IOException {
    HashingSchemes hs = new HashingSchemes();
    Collisions col = new Collisions();
    int[][] hashTblLinear;
    int[][] hashTblQuad;
    Node[] hashTblChain;
    int loc;
    int key;
    boolean openSlotLinear;
    boolean openSlotQuad;
    boolean openSlotChain;
    int numBuckets = (120/bucketSize) + 1;

    //create hash tables for each type of collision handling
    if(bucketSize > 1) {
      hashTblLinear = new int[numBuckets][bucketSize];
      hashTblQuad = new int[numBuckets][bucketSize];
      hashTblChain = new Node[numBuckets];
    }
    else{
      hashTblLinear = new int[120][1];
      hashTblQuad = new int[120][1];
      hashTblChain = new Node[120];
    }
    //Sets all bucket slots to -1
    for (int m = 0; m < hashTblLinear.length; m++) {
      Node node = new Node(-1,m);
      hashTblChain[m] = node;
      for (int n = 0; n < bucketSize; n++) {
        hashTblLinear[m][n] = -1;
        hashTblQuad[m][n] = -1;
      }
    }

    //send key and hash table to the appropriate hashing scheme and collision
    //handling technique
    int i = 0;
    while (inputVals[i] != -1) {
      key = Math.abs(inputVals[i]); //absolute value prevents negative key values

      openSlotLinear = false;//boolean values keep track of whether collision handling
      openSlotQuad = false;//is necessary
      openSlotChain = false;

      if (divisor == 0) {
        loc = hs.Midsquare(key);
      }
      else {
        loc = hs.Divide(key, Math.abs(divisor));
      }
      /*
      This series of if statements checks each separate hash table to determine
      if collision handling will be necessary based on the initial hash location
      */
      for (int j = 0; j < bucketSize; j++) { //check every slot in current bucket
        if (hashTblLinear[loc][j] == -1 && !openSlotLinear){
          hashTblLinear[loc][j] = key;
          openSlotLinear = true;
        }
        if (hashTblQuad[loc][j] == -1 && !openSlotQuad){
          hashTblQuad[loc][j] = key;
          openSlotQuad = true;
        }
      }
      if (hashTblChain[loc].bucket == -1){
        hashTblChain[loc].bucket = key;
        col.push(key);
        openSlotChain = true;
      }

      //send key to collision handling if hashed bucket is full
      if (!openSlotLinear) {
        hashTblLinear = col.LinearProbe(key, hashTblLinear, loc);
      }
      if (!openSlotQuad) {
        hashTblQuad = col.QuadProbe(key, hashTblQuad, loc);
      }
      //chaining store the key and location value and performs collision resolution
      //after all non-colliding values have been stored
      if (!openSlotChain && bucketSize == 1) {
        col.storeChain(key,loc);
      }

      i++;
    }//end while

    //if chaining collision resolution is necessary, it is done now and the
    //amended chaining hash table is returned
    if (bucketSize == 1) {
      hashTblChain = col.Chaining(hashTblChain);
    }
    //send hash tables and other values of interest to be printed
    col.printHashTbl(hashTblLinear, hashTblQuad, hashTblChain, inputVals, i, output, divisor, bucketSize);

  }

}//end class
