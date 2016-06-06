# word count in storm
Steps:
1. A series of English sentences are built in Spout, and randomly emitted.
2. Use one bolt to receive the tuples from Spout and then normalization processing is carried out, 
which means splitting the sentence to words and emit the words.
3. Group by fields, receive the words emitted from last bolt, and do word frequency counting, sorting, emitting.
4. Print out the results in real time. 

# word count in MapRduce vs storm
1. Data characteristics: both are massive, while it's in fixed scale in MapReduce, but in Storm it's increasing continuingly.
2. Instance characteristics: MapReduce->batching processing, Storm->real-time streaming processing
3. Ideas behind: both are divide-and-conquer
4. Technical model: Hadoop vs Storm
5. Programming model: MapReduce uses map and reduce, while storm uses spout and bolt