# StockDemo

```
Three functionalities of this code:
1. Get 2 lowest stock prices of each company.
2. Get sum price of each company.
3. Get average stock price of each company.
```

```
running commands: 
[cloudera@quickstart Stock]$ mvn clean
[cloudera@quickstart Stock]$ mvn package
[cloudera@quickstart Stock]$ hadoop  fs -mkdir /user/cloudera/stock/ 
[cloudera@quickstart Stock]$ hadoop  fs -mkdir /user/cloudera/stock/input
[cloudera@quickstart Stock]$ hadoop fs -put stock /user/cloudera/stock/input/1
[cloudera@quickstart Stock]$ hadoop  fs -mkdir /user/cloudera/stock/output
[cloudera@quickstart Stock]$ hadoop jar target/Stock-0.0.1-SNAPSHOT.jar Demo.StockPrice /user/cloudera/stock/input/1 /user/cloudera/stock/output/1
```