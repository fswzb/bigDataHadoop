-- Hive queries for Word Count

DROP TABLE IF EXISTS doc;

-- 1) create table to load whole file
CREATE TABLE doc(text STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\n' STORED AS textfile;

--2) loads plain text file
--if file is .csv then in replace '\n' by ',' in step no 1 (creation of doc table)
LOAD DATA INPATH '/user/guan01/hive/wordcount/' OVERWRITE INTO TABLE doc;

-- 3) wordCount in single line
SELECT word, COUNT(*) AS cnt FROM doc LATERAL VIEW explode(split(text, ' ')) lTable AS word GROUP BY word ORDER BY cnt DESC LIMIT 200;
