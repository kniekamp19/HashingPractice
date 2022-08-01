/*
Kristen Niekamp
Lab 2 - Hashing

This class contains methods for collision handling using Linear Probing,
Quadratic Probing, and Chaining within the table.
*/

import java.util.*;
import java.io.*;

class Collisions {
  /*
  These arrays and variables will keep track of values unable to be stored
  in the hash tables. The stack and top pointer will keep track of
  the amount of space remaining in the chaining hash table.
  */
  private int[] stack = new int[1000];
  int top = -1;
  int[] extraLinear = new int[120];
  int extraTopLinear = 0;
  int[] extraQuad = new int[120];
  int extraTopQuad = 0;
  int[] extraChain = new int[120];
  int[] extraChainLocs = new int[120];
  int extraTopChain = 0;
  int linearCols = 0;//these variables count the number of collisions
  int quadCols = 0;
  int chainCols = 0;

  public Collisions() {
    for (int i = 0; i < extraChain.length; i++) {
      extraChain[i] = -1;
    }
  }

/*
This method handles linear probing collision handling
If the current location is not available for key insertion, the location
is updated by increasing the index by 1.
*/
  public int[][] LinearProbe(int key, int[][] tbl, int loc) {
    int i = 1;
    while (i < tbl.length) {
      //reset location to first bucket if at end of hash table
      if (loc == tbl.length) {
        loc = 0;
      }

      //check each slot of the current bucket for availability
      for (int j = 0; j < tbl[loc].length; j++) {
        if (tbl[loc][j] == -1) {
          tbl[loc][j] = key;
          return tbl;//if an available slot is found, return the table
        }
        // linearCols++;
      }
      linearCols++;
      i++;
      loc++;//if no available slot, increase bucket index by 1 and check
    }

    //if every bucket in hash table has been checked and is full,
    //store the value in the extra values linear array and return the hash table
    if (i >= tbl.length) {
      extraLinear[extraTopLinear] = key;
      extraTopLinear++;
    }

    return tbl;
  }

/*
This method handles quadratic probing collision handling
If a location is not available for key insertion, the location is updated
using the quadratic probing formula h'(k) = h(k) + c1(i) + c2(i^2)
*/
  public int[][] QuadProbe(int key, int[][] tbl, int loc) {
    int i = 1;
    while (i < tbl.length) {

      for (int j = 0; j < tbl[loc].length; j++) {
        if (tbl[loc][j] == -1) {
          tbl[loc][j] = key;
          return tbl;
        }
        //quadCols++;
      }//end for
      quadCols++;

      //location increases quadratically with each iteration of the while loop
      //each jump within the table is larger than the last one while searching
      //for an open slot
      loc = (int)((loc + (0.5 * i) + (0.5 * (i * i))) % tbl.length);
      i++;
    }//end while

    //if every bucket in hash table has been checked and is full,
    //store the value in the extra values qudratic array and return the hash table
    if (i >= tbl.length) {
      extraQuad[extraTopQuad] = key;
      extraTopQuad++;
    }
    return tbl;
  }

/*
This method handles open addressing chaining collision handling
It uses the attributes of each node to point to the next slot in the chain
*/
  public Node[] Chaining(Node[] tbl) {

    Node currNode;
    int x;
    int loc;
    int key;
    int n = 0;

    //checks if there are any collided values to chain
    if (extraTopChain == 0) {
      return tbl;
    }

    //while there are collided values left to chain
    while(extraChain[n] != -1) {
      x = 0;
      key = extraChain[n];
      loc = extraChainLocs[n];
      //the first node is the initial hash location from the hash function
      currNode = tbl[loc];

      push(key); //add key to top of stack and update top pointer
      if (top >= 120) {//check if hash table is full
        return tbl;
      }

      //check table for available slot
      while(currNode.bucket != -1 && x <= tbl.length) {
        chainCols++;//count collisions
        //traverse through chained list of nodes that collided on initial hash location
        if (currNode.next != null) {
          currNode = currNode.next;
          loc = currNode.index;
        }
        //find next available slot and update next pointer of previous node
        else{
          loc++;//chaining applied to simple linear probing technique
          x++;
          if (loc == tbl.length) {
            loc = 0;
          }
          else if (tbl[loc].bucket != -1) {
            continue; //only update pointer if slot is available for current key
          }
          currNode.next = tbl[loc];//update next pointer of last node in singly-linked list
          currNode = currNode.next;
        }//end else
      }//end while
      currNode.bucket = key;//place key in current node's empty bucket slot
      extraChain[n] = -1;//replace key in collision list with -1 flag to mark empty spot
      n++;//index value to traverse through collision list
    }//end outer while

    return tbl;
  }//end chaining

/*
Push key onto stack for chaining purposes
This stack keeps track of free space in hash table using chaining
*/
  public void push(int key) {
    top++;

    stack[top] = key;
  }

/*
Store the key and initial hash location of a collided value
to be used in chaining once all values with no collisions are stored
*/
  public void storeChain(int key, int loc){
    extraChain[extraTopChain] = key;
    extraChainLocs[extraTopChain] = loc;
    extraTopChain++;
  }

/*
Checks if input array contains key values
*/
  public boolean isEmpty(int[] tbl) {
    for (int i = 0; i < tbl.length; i++) {
      if (tbl[i] != -1) {//-1 flag marks empty spaces; all key values are positive
        return false;
      }
    }
    return true;
  }

/*
This method prints the input values, hash tables, and collision statistics.
Hash tables are separated by probing type.
*/
  public void printHashTbl(int[][] hashTblLinear, int[][] hashTblQuad, Node[]hashTblChaining, int[] input, int lastInput, BufferedWriter output, int divisor, int bucketSize) throws IOException {
    int newLine = 0;
    int i = 0;
    output.write("Divisor: " + divisor + " (A divisor value of 0 means Midsquare hashing was used)\n");
    output.write("Bucket Size: " + bucketSize + "\n\n");

    //print input values
    output.write("Input values:\n");
    while(input[i] != -1) {
      output.write(input[i] + "  ");
      i++;
      if (i % 5 == 0 && i != 0) {
        output.newLine();
      }
    }

    output.write("\nValues of -1 indicate an empty bucket slot.\n\n");

    if (hashTblLinear[0].length == 1) { //bucket of size 1: print linear probing, quadratic probing, and chaining
      //print linear probing hash table
      output.write("Collision handling: Linear Probing\nHash table with bucket size 1:\n");
      for (int m = 0; m < hashTblLinear.length; m++) {
        output.write(hashTblLinear[m][0] + "  ");
        if (m % 5 == 4) {
          output.newLine();
        }
      }
      //print values that could not be stored in hash table
      output.write("\nNumber of collisions using Linear Probing: " + linearCols + "\n");
      if (extraTopLinear != 0) {
        output.write("Values not stored in hash table:\n");
        for (int x = 0; x < extraTopLinear; x++) {
          output.write(extraLinear[x] + "  ");
          if (x%5 == 4) {
            output.newLine();
          }
        }
      }
      //print quadratic probing hash table
      output.write("\n\nCollision handling: Quadratic Probing\nHash table with bucket size 1:\n");
      for (int m = 0; m < hashTblQuad.length; m++) {
        output.write(hashTblQuad[m][0] + "  ");
        if (m % 5 == 4) {
          output.newLine();
        }
      }
      //print values that could not be stored in hash table
      output.write("\nNumber of collisions using Quadratic Probing: " + quadCols + "\n");
      if (extraTopQuad != 0) {
        output.write("Values not stored in hash table:\n");
        for (int x = 0; x < extraTopQuad; x++) {
          output.write(extraQuad[x] + "  ");
          if (x%5 == 4) {
            output.newLine();
          }
        }
      }
      //print chaining hash table
      output.write("\n\nCollision handling: Chaining\nHash table with bucket size 1:\n");
      for (int m = 0; m < hashTblChaining.length; m++) {
        output.write(hashTblChaining[m].bucket + "  ");
        if (m % 5 == 4) {
          output.newLine();
        }
      }
      output.write("\nNumber of collisions using Chaining: " + chainCols + "\n");
      //print values that could not be stored in hash table
      if (!isEmpty(extraChain)) {
        output.write("Values not stored in hash table:\n");
        for (int x = 0; x < extraChain.length; x++) {
          if (extraChain[x] != -1) {
            output.write(extraChain[x] + "  ");
            newLine++;
            if (newLine%5 == 0 && newLine != 0) {
              output.newLine();
            }
          }
        }
      }
    }

    //if bucket size > 1: print linear probing and quadratic probing hash tables
    else{
      output.write("\n\nCollision handling: Linear Probing\nHash table with bucket size " + hashTblLinear[0].length + ":");
      for (int m = 0; m < hashTblLinear.length; m++) {
        output.newLine();
        for (int n = 0; n < hashTblLinear[0].length; n++) {
          output.write(hashTblLinear[m][n] + "  ");
        }
      }
      output.write("\nNumber of collisions using Linear Probing: " + linearCols + "\n");
      if (extraTopLinear != 0) {
        output.write("Values not stored in hash table:\n");
        for (int x = 0; x < extraTopLinear; x++) {
          output.write(extraLinear[x] + "  ");
          if (x%hashTblLinear[0].length == 0) {
            output.newLine();
          }
        }
      }

      output.write("\n\nCollision handling: Quadratic Probing\nHash table with bucket size " + hashTblQuad[0].length + ":");
      for (int m = 0; m < hashTblQuad.length; m++) {
        output.newLine();
        for (int n = 0; n < hashTblQuad[0].length; n++) {
          output.write(hashTblQuad[m][n] + "  ");
        }
      }
      output.write("\n\nNumber of collisions using Quadratic Probing: " + quadCols + "\n");
      if (extraTopQuad != 0) {
        output.write("Values not stored in hash table:\n");
        for (int x = 0; x < extraTopQuad; x++) {
          output.write(extraQuad[x] + "  ");
          if (x%hashTblQuad[0].length == 0) {
            output.newLine();
          }
        }
      }

    }//end else
  }//end printhashtbl

}//end class
