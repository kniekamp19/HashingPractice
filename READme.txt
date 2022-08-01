This project was created for Algorithms for Bioinformatics at Johns Hopkins University 
as part of the AAP M.S. Bioinformatics degree plan.
This program takes an input file of integers and hashes each integer using either a
midsquare or modulo hashing scheme.

The source code in this assignment can be compiled with Java SE 8 from the command line.
It was constructed using Atom v1.53.0.

To run the program, enter: java HashingMain [inputFile].txt [outputFile.txt] (int)[divisor] (int)[bucketSize]

Note: If output file already exists, new output will be appended to existing file.

For Midsquare hashing, enter a divisor value of 0. For modulo division hashing, enter an integer greater than 0
that is a factor of 120. 
The maximum number of bucket slots is 120. Any bucketsize > 1 will divide the number of
buckets by that amount (i.e. a bucket size of 2 = 120/2 = 60 buckets of size 2.

Each separate input value must be on its own line. Values are processed by line. Any lines in the input file 
containing letters or that are empty will be ignored. Only lines with numbers will be processed.