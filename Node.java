/*
Kristen Niekamp
Lab 2 - Hashing

This class creates nodes to be used as buckets for open address chaining
handling technique. Each node contains a bucket for the key value
and a node.next pointer that points to the next location in the table
for a collision.
*/

class Node{ //make nodes for chaining collision handling
  int bucket;
  Node next;
  int index;

  //Constructor to make a new node
  Node(int b, int i) {
    bucket = b; //node bucket will hold key
    index = i;
    next = null;//node.next will point to next collided value in table
  }
}
