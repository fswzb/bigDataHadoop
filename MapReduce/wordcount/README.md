# steps:

hadoop fs -copyFromLocal input.txt /user/cloudera/input.txt

hadoop jar target/word-count-1.0.0.jar com.neutronmobile.word_count.WordCount /user/cloudera/input.txt /user/cloudera/output

hadoop fs -cat /user/cloudera/output/*
