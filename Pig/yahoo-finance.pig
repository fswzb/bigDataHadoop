/*
pigLatin code for processing yahoo finance data
*/

--load data

REGISTER '/home/guan01/piggybank.jar';  (local)

stocks = LOAD '/user/guan01/pig/yahooFinance.csv' USING org.apache.pig.piggybank.storage.CSVExcelStorage(',', 'NO_MULTILINE', 'UNIX', 'SKIP_INPUT_HEADER') AS (date: chararray, open: float, high: float, low: float, close: float, volumn: float, adjClose: float);

dump stocks;

--check schema
describe stocks;


--store
store stocks into '/user/guan01/pig/stocks';
hadoop fs -cat /user/guan01/pig/stocks/*

--filter(find stocks that have close price than 40.0)
closeGreaterThan40 = FILTER stocks by close >= 40.0;
dump closeGreaterThan40;

-- filter high < 35 and in 2016 stocks
highLessThan35in2016 = FILTER stocks by high < 35.0 and date > '2015-12-31';
dump highLessThan35in2016;

-- filter 2015 stocks
stocks2015 = FILTER stocks BY $0 MATCHES '.*2015.*'; 
dump stocks2015; 

-- filter high and low difference greater than 1 or high greater than 50
highLowDiffGreaterThan1orHighGreaterThan50 = FILTER stocks BY (high > 50.0) OR (NOT (high - low < 1.00));
dump highLowDiffGreaterThan1orHighGreaterThan50;




--foreach generate
adjClosePlusTenCents = foreach closeGreaterThan40 generate (date, open, high, low, close, volumn, adjClose+0.10);
dump adjClosePlusTenCents;


--sampling data
sampleStock = SAMPLE stocks 0.01;
store sampleStock into '/user/guan01/pig/sampleStock';
hadoop fs -cat /user/guan01/pig/sampleStock/*



--order
stockOrdered = order stocks by close desc;
top10  = limit stockOrdered 10;
dump top10;


--limit
stock10 = limit stocks 10;
dump stock10;


--split
split stocks into group1 if close < 30, group2 if close > 40, group3 otherwise;
dump group1;
dump group2;     
dump group3;


--unoin
stocksLessOrEqual40 = union group1, group3;
dump stocksLessOrEqual40;
