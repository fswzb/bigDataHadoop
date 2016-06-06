# simple restaurant recommandation system using sparkSQL

1. Code a web crawler using Yelp Search API, and save the data into json format (file name: myfileObj.json). Then upload it to HDFS.
2. Based on restaurant ratings and number of review counts, select 5 restaurant:

goodRes = sqlContext.sql("SELECT title, city, review_count FROM business WHERE rating >= 4.5 ORDER BY review_count DESC LIMIT 5")


Note: this system is for new users, which only considered restaurant rating and number of reviews
