import re
import json
from pyspark import SparkConf, SparkContext
from pyspark.sql import SQLContext, Row
#import collections
conf = SparkConf().setMaster("local").setAppName("SparkSQL")
sc = SparkContext(conf = conf)
sqlContext = SQLContext(sc)

biz = sqlContext.read.json("user/cloudera/Yelp/myfileObj.json")
biz.printSchema()
biz.registerTempTable("business")

# SQL can be run over DataFrames that have been registered as a table.
goodRes = sqlContext.sql("SELECT title, city, review_count FROM business WHERE rating >= 4.5 ORDER BY review_count DESC LIMIT 5")


# The results of SQL queries are RDDs and support all the normal RDD operations.
for res in goodRes.collect():
  print(res)
