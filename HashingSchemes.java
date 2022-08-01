/*
Kristen Niekamp
Lab 2 - Hashing

This class contains the methods for each hashing scheme - Modulo Division
and Midsquare with parameters for bucket size and divisor value.
*/

import java.util.*;

class HashingSchemes {

/*
This method divides the input key by the divisor
to find the first hashing location
*/
  public int Divide(int key,int div){
    int loc = key%div;
    return loc;
  }

/*
This method uses a modified midsquare hashing function to find the initial
hash location. The input key is squared and the middle two values
are taken as the hash table location.
The modification decreases the key by one order of magnitude if squaring
it places the key^2 value beyond the maximum int limit of Java.
If the key^2 value is only 3 digits, the first two digits are taken.
If the key^2 value is 1 digit, that digit is used as the hash key.
*/
  public int Midsquare(int key) {
    while (key > 46340) {
      key = key/10;
    }
    int locSquare = key * key;
    int mid;
    String locString;
    int loc;

    if (locSquare < 10) {
      loc = locSquare;//value isn't large enough to take 2 digits
    }
    else {
      locString = Integer.toString(locSquare);
      mid = locString.length() / 2;
      loc = Integer.parseInt(locString.substring(mid-1,mid+1));
    }
    return loc;
  }

}//end class
