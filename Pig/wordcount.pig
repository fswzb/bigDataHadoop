/*
wordcount code
*/

a = load '/user/jason/data/yoga.txt';
b = foreach a generate flatten(TOKENIZE((chararray)$0)) as word;
c = group b by word;
d = foreach c generate COUNT(b), group;
store d into '/user/jason/pig_wordcount';
