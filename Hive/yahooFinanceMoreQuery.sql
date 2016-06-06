-- 1. prepare file
hadoop fs -mkdir /user/guan01/hive/yahooFinance
scp -P 49233 amazon.csv guan01@rm.neutronmobile.com:~/
scp -P 49233 yahoo.csv guan01@rm.neutronmobile.com:~/
hadoop fs -copyFromLocal amazon.csv /user/guan01/hive/stockDemo
hadoop fs -copyFromLocal yahoo.csv /user/guan01/hive/stockDemo
hadoop fs -ls /user/guan01/hive/stockDemo


-- 2. beeline connection
!connect jdbc:hive2://192.168.1.13:10000/default


-- 3. create databases
CREATE DATABASE IF NOT EXISTS guan_db;

-- select database and show tables
USE temp;
USE guan_db;
SHOW TABLES;


-- 4. create yahooFinance table
DROP TABLE IF EXISTS stockAmazon;
DROP TABLE IF EXISTS stockYahoo;


-- Hive provides DATE and TIMESTAMP data types for date related fields
-- DATE values are represented in the form YYYY-MM-DD. Date ranges allowed
-- are 0000-01-01 to 9999-12-31
-- TIMESTAMP uses the format yyyy-mm-dd hh:mm:ss

CREATE TABLE stockAmazon (
        stockDate DATE,
        open FLOAT,
        high FLOAT,
        low FLOAT,
        close FLOAT,
        volume BIGINT,
        adjClose FLOAT
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS textfile  -- TERMINATED BY ',' since it's csv file 
tblproperties ("skip.header.line.count"="1"); // skip the header count 

CREATE TABLE stockYahoo (   
        stockDate DATE,
        open FLOAT,
        high FLOAT,
        low FLOAT,
        close FLOAT,
        volume BIGINT,
        adjClose FLOAT
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS textfile  
tblproperties ("skip.header.line.count"="1"); // skip the header count 

DESCRIBE FORMATTED stockamazon;
-- MANAGED / INTERNAL TABLE
hadoop fs -ls /apps/hive/warehouse/guan_db.db
DROP TABLE stockamazon;
SHOW TABLES;

-- RECREATE stockamazon;

-- Load data into table
LOAD DATA INPATH '/user/guan01/hive/stockDemo/amazon.csv' OVERWRITE INTO TABLE stockAmazon;
LOAD DATA INPATH '/user/guan01/hive/stockDemo/yahoo.csv' OVERWRITE INTO TABLE stockYahoo;



-- 5. QUERY

-- Check how many rows are inserted
SELECT COUNT(*) FROM YahooFinance;

-- Select yahoo stock info of Dec. 2012
SELECT * FROM stockYahoo WHERE stockDate > '2002-11-30' AND stockDate < '2003-01-01';

-- Find the top 10 open price for yahoo
-- The difference between "order by" and "sort by" is that the former guarantees total order in 
-- the output while the latter only guarantees ordering of the rows within a reducer. If there are 
-- more than one reducer, "sort by" may give partially ordered final results.
SELECT open FROM stockYahoo SORT BY open DESC LIMIT 10;
SELECT open FROM stockYahoo ORDER BY open DESC LIMIT 10;

-- Check amazon's average close price since 2010
SELECT avg(close) FROM stockAmazon WHERE stockDate > '2009-12-31';

-- Check the max close price diff for amazon this year 
SELECT (max(close) - min(close)) as maxCloseDiff FROM stockAmazon WHERE stockDate > '2015-12-31';

SELECT max(close) as maxClose FROM stockAmazon WHERE stockDate > '2015-12-31';
SELECT min(close) as minClose FROM stockAmazon WHERE stockDate > '2015-12-31';

-- Find Amazon's price info on the first day of each month
SELECT * FROM stockAmazon where day(stockDate) = 1;
SELECT * FROM stockAmazon where stockDate LIKE '%-01';

-- Join: find the dates when both Amazon's and Yahoo's stock prices increased more than 10%
SELECT stockYahoo.stockDate, stockYahoo.open, stockYahoo.close, stockAmazon.open, stockAmazon.close
FROM stockYahoo JOIN stockAmazon ON (stockYahoo.stockDate = stockAmazon.stockDate) 
WHERE (stockYahoo.close - stockYahoo.open)/stockYahoo.open > 0.10
AND (stockAmazon.close - stockAmazon.open)/stockAmazon.open > 0.10;

-- Union:
-- Find the bottom 10 close prices for amazon and yahoo
SELECT close FROM (SELECT close FROM stockAmazon ORDER BY close LIMIT 10) amazonClose
UNION ALL
SELECT close FROM (SELECT close FROM stockYahoo ORDER BY close LIMIT 10) yahooClose;


-- 6. External vs Internal Table
-- The EXTERNAL keyword lets you create a table and provide a LOCATION so that Hive does not use a 
-- default location for this table. This comes in handy if you already have data generated. When 
-- dropping an EXTERNAL table, data in the table is NOT deleted from the file system.
-- when you drop a table, if it is managed table hive deletes both data and meta data,
-- if it is external table Hive only deletes metadata.

hadoop fs -mkdir -p /user/guan01/hive/hiveDemo/external
hadoop fs -copyFromLocal amazon.csv /user/guan01/hive/hiveDemo/external/


DROP TABLE IF EXISTS stockAmazonExternal;

CREATE EXTERNAL TABLE IF NOT EXISTS stockAmazonExternal (
        stockDate DATE,
        open FLOAT,
        high FLOAT,
        low FLOAT,
        close FLOAT,
        volume BIGINT,
        adjClose FLOAT
        )
COMMENT 'This is the External Yahoo Finance Table'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' STORED AS textfile
location '/user/guan01/hive/hiveDemo/external/';

SHOW TABLES;
-- See table type
DESCRIBE FORMATTED stockAmazonExternal;

-- Drop the table stockAmazonExternal
DROP TABLE IF EXISTS stockAmazonExternal;
SHOW TABLES;

-- Check if the file is still there
hadoop fs -ls /user/guan01/hive/hiveDemo/external


-- Change a table from external to internal.
ALTER TABLE stockAmazonExternal SET TBLPROPERTIES('EXTERNAL'='FALSE');

-- Change a table from internal to external.
ALTER TABLE stockAmazonExternal SET TBLPROPERTIES('EXTERNAL'='TRUE');


