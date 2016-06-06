"""
The script is from pyspark example directory when spark is downloaded. 
In practice, people may prefer to use the KMeans algorithm in MLlib, 
as shown in examples/src/main/python/mllib/kmeans.py.
"""
from __future__ import print_function
import sys
import numpy as np
from pyspark import SparkContext

# convert data to float type
def parseVector(line):
    return np.array([float(x) for x in line.split(' ')])

# calculate which cluster should the point go to and return the cluster index
def closestPoint(p, centers):
    bestIndex = 0
    closest = float("+inf")
    for i in range(len(centers)):
        tempDist = np.sum((p - centers[i]) ** 2)
        if tempDist < closest:
            closest = tempDist
            bestIndex = i
    return bestIndex


if __name__ == "__main__":

    if len(sys.argv) != 4:
        # K: number of clusters
        # convergeDist: converging condition
        print("Usage: kmeans <file> <k> <convergeDist>", file=sys.stderr)
        exit(-1)

    print("""WARN: This is a naive implementation of KMeans Clustering and is given
       as an example! Please refer to examples/src/main/python/mllib/kmeans.py for an example on
       how to use MLlib's KMeans implementation.""", file=sys.stderr)

    sc = SparkContext(appName="PythonKMeans")
    # lines = sc.textFile(sys.argv[1])
    lines = sc.textFile("kmeansInput.txt")
    # local path
    lines = sc.textFile("file:///home/cloudera/Desktop/kmeansInput.txt")
    
    # Spark also supports pulling data sets into a cluster-wide in-memory cache. 
    # This is very useful when data is accessed repeatedly, such as when querying 
    # a small “hot” dataset or when running an iterative algorithm like PageRank. 
    # As a simple example, let’s mark our linesWithSpark dataset to be cached:
    data = lines.map(parseVector).cache()
    K = int(sys.argv[2])
    K = 2
    # iteration will be stopped if the distance is less than threshold value
    convergeDist = float(sys.argv[3])

    # sample K values
    kPoints = data.takeSample(False, K, 1)
    tempDist = 1.0

    # run if distance is larger than threshold value
    while tempDist > convergeDist:
        # run map process for all the data and rdd (index, (point, 1)) will be created
        closest = data.map(
            lambda p: (closestPoint(p, kPoints), (p, 1)))
        # run reduce and the purpose is to recalculate the centers, which also create rdd
        pointStats = closest.reduceByKey(
            lambda p1_c1, p2_c2: (p1_c1[0] + p2_c2[0], p1_c1[1] + p2_c2[1]))
        # create new center points
        newPoints = pointStats.map(
            lambda st: (st[0], st[1][0] / st[1][1])).collect()
        # calculate the distance between old center and new center
        tempDist = sum(np.sum((kPoints[iK] - p) ** 2) for (iK, p) in newPoints)
        # set new centers
        for (iK, p) in newPoints:
            kPoints[iK] = p

    print("Final centers: " + str(kPoints))

    sc.stop()
