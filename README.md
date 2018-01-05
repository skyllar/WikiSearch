# WikiSearch

The project implements a Wikipedia-Based Search Engine involving indexing of 46.7 GB WikiXML Dump with following features:
1. Index compression techniques for faster retrieval (150ms).
2. Document ranking algorithms for relevant results.

Functional Overview:

A. Preprocessing and Indexing of the Wiki dump:
  1. Some key components involved from the preprocessing perspective:
    a. Spotting of the tagName in documents.
    b. Tokenize texts in the sample corpus.
    c. Stop word removal and case folding.
    d. Stemming.
   
  2. Other main component is indexing handled in Indexing.java with following data structures involved:
    a. HashMap as the outer data structure to store the words in the corpus.
    b. Sort the HashMap to store the data lexographically in the index file. This is necessary to optimise search operation.
    c. Hashmap values contain references to the inner data structure which is a "TreeMap" to store document Ids in sorted format.

B. Searching:
  1. Preprocess keyword to be searched (i.e. stemming, etc).
  2. Look for the preprocessed keyword in the index file (binary search used for this).
  3. Apply some document ranking algorithms (like Tf/Idf) and return the most relevant search result in 150ms on an average.
