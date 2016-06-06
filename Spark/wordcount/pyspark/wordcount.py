from __future__ import print_function

import sys
from operator import add

from pyspark import SparkContext


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: wordcount <file>", file=sys.stderr)
        exit(-1)
    sc = SparkContext(appName="PythonWordCount")
    lines = sc.textFile(sys.argv[1], 1)
    # HDFS path
    lines = sc.textFile("input.txt")
    # local path: file:// + absolute path
    lines = sc.textFile("file:///home/cloudera/Desktop/input4.txt")
    # The input function to map returns a single element, while the flatMap returns 
    # a list of elements (0 or more). And also, the output of the flatMap is flattened. 
    # http://www.dattamsha.com/2014/09/map-vs-flatmap-spark/
    counts = lines.flatMap(lambda x: x.split(' ')) \
		          .filter(lambda x: len(x) > 0 ) \
		          .map(lambda x: x.lower()) \
                  .map(lambda x: (x, 1)) \
                  .reduceByKey(add) \
		          .map(lambda (x, y): (y, x)) \
		          .sortByKey(True) 
    output = counts.collect()
    with open('output.txt', 'w') as wordCountWriter:
    	for (word, count) in output:
        	#print("%s: %i" % (word, count))
		wordCountWriter.write("%s: %i" % (count, word) + '\n')

    sc.stop()
