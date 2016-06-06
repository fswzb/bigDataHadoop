## Step 1. Let’s load a data file into a Hive table.

mkdir -p /tmp/test/data_local/input/hive/twitter/
cd /tmp/test/data_local/input/hive/twitter/
wget http://hortonassets.s3.amazonaws.com/tutorial/hive/Twitterdata.txt

hadoop fs -mkdir -p /tmp/test/data_hdfs/input/hive/twitter/
hadoop fs -copyFromLocal /tmp/test/data_local/input/hive/twitter/Twitterdata.txt /tmp/test/data_hdfs/input/hive/twitter/



USE temp;
CREATE TABLE TwitterExampletextexample(
        tweetId BIGINT,
        username STRING,
        txt STRING,
        CreatedAt STRING,
        profileLocation STRING,
        favc BIGINT,
        retweet STRING,
        retcount BIGINT,
        followerscount BIGINT
        )
COMMENT 'This is the Twitter streaming data'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;

LOAD DATA INPATH '/tmp/test/data_hdfs/input/hive/twitter/Twitterdata.txt' OVERWRITE INTO TABLE TwitterExampletextexample;

## Step 2. Let’s create a table using RCfile format

CREATE TABLE TwitterExampleRCtable(
        tweetId BIGINT, username STRING,
        txt STRING, CreatedAt STRING,
        profileLocation STRING COMMENT 'Location of user',
        favc BIGINT,retweet STRING,retcount BIGINT,followerscount BIGINT)
COMMENT 'This is the Twitter streaming data'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS RCFILE;

INSERT OVERWRITE TABLE TwitterExampleRCtable select * from  TwitterExampletextexample;

## Step 3. Let’s query the table we just created.

SELECT profileLocation, COUNT(txt) as count1
FROM TwitterExampleRCtable
GROUP BY profileLocation
ORDER BY count1 desc limit 10;

## Step 4. Let’s look at Managed tables vs External tables
hadoop fs -mkdir -p /tmp/test/data_hdfs/input/hive/twitter_external/
hadoop fs -copyFromLocal /tmp/test/data_local/input/hive/twitter/Twitterdata.txt /tmp/test/data_hdfs/input/hive/twitter_external/

CREATE EXTERNAL TABLE IF NOT EXISTS ExternalExample(
        tweetId BIGINT, username STRING,
        txt STRING, CreatedAt STRING,
        profileLocation STRING,
        favc BIGINT,retweet STRING,retcount BIGINT,followerscount BIGINT)
COMMENT 'This is the Twitter streaming data'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
location '/tmp/test/data_hdfs/input/hive/twitter_external/';

describe formatted TwitterExampletextexample;
describe formatted ExternalExample;

## Step 5. Hive ORC File format.
CREATE TABLE ORCFileFormatExample(
    tweetId BIGINT, username STRING,
    txt STRING, CreatedAt STRING,
    profileLocation STRING COMMENT 'Location of user',
    favc INT,retweet STRING,retcount INT,followerscount INT)
COMMENT 'This is the Twitter streaming data'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS ORC tblproperties ("orc.compress"="ZLIB");

## Step 6. Let’s create a PARTITIONED Table and load data into

CREATE TABLE PARTITIONEDExample(
      tweetId BIGINT,
      username STRING,
      txt STRING,
      favc BIGINT,
      retweet STRING,
      retcount BIGINT,
      followerscount BIGINT
      )
COMMENT 'This is the Twitter streaming data'
PARTITIONED BY(CreatedAt STRING, profileLocation STRING)
ROW FORMAT DELIMITED FIELDS
TERMINATED BY '\t' STORED AS TEXTFILE;

INSERT OVERWRITE TABLE PARTITIONEDExample
PARTITION (CreatedAt="2015_12_12_14",profileLocation="Chicago")
SELECT tweetId,username,txt,favc,retweet,retcount,followerscount
FROM ExternalExample
where profileLocation='Chicago' limit 100;

INSERT OVERWRITE TABLE PARTITIONEDExample
PARTITION (CreatedAt="2015_12_12_14",profileLocation="Bay Area, CA")
SELECT tweetId,username,txt,favc,retweet,retcount,followerscount
FROM ExternalExample
where profileLocation='Bay Area, CA' limit 100;


SELECT profileLocation, SUM(followerscount) as followerscounts
FROM twitterexampletextexample
GROUP BY profileLocation
ORDER BY followerscounts DESC;

ALTER TABLE PARTITIONEDExample DROP IF EXISTS PARTITION (CreatedAt="2015_12_12_14",profileLocation="Bay Area, CA");


########

##. load data, join and left joins, table and external tables, add and drop partitions.



hadoop fs -mkdir -p /tmp/test/data_hdfs/input/hive/sample/sample_07
hadoop fs -mkdir -p /tmp/test/data_hdfs/input/hive/sample/sample_08
hadoop fs -copyFromLocal /tmp/sample_07 /tmp/test/data_hdfs/input/hive/sample/sample_07
hadoop fs -copyFromLocal /tmp/sample_08 /tmp/test/data_hdfs/input/hive/sample/sample_08



CREATE EXTERNAL TABLE sample_07(
code string,
description string,
total_emp int,
salary int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS INPUTFORMAT
'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
'/tmp/test/data_hdfs/input/hive/sample/sample_07';


CREATE EXTERNAL TABLE sample_08(
code string,
description string,
total_emp int,
salary int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS INPUTFORMAT
'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
'/tmp/test/data_hdfs/input/hive/sample/sample_08';


SELECT
sample_08.code,
sample_08.salary,
sample_07.salary,
100* (sample_08.salary - sample_07.salary)/sample_07.salary as percentage
FROM sample_08
JOIN sample_07 ON sample_08.code = sample_07.code
where sample_08.salary > sample_07.salary;


SELECT
sample_08.code,
sample_08.salary,
sample_07.salary
FROM sample_08
LEFT OUTER JOIN sample_07 ON sample_08.code = sample_07.code
where sample_07.salary is null;

SELECT * FROM sample_07 WHERE code = '53-7777';

CREATE EXTERNAL TABLE sample_all(
code string,
description string,
total_emp int,
salary int)
partitioned by (year string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS INPUTFORMAT
'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
'/tmp/test/data_hdfs/input/hive/sample/';

ALTER TABLE sample_all ADD IF NOT EXISTS PARTITION (year='07') LOCATION '/tmp/test/data_hdfs/input/hive/sample/sample_07';
ALTER TABLE sample_all ADD IF NOT EXISTS PARTITION (year='08') LOCATION '/tmp/test/data_hdfs/input/hive/sample/sample_08';


SELECT code, year FROM sample_all WHERE code = '53-7777';
