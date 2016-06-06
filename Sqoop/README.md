# Goal
The goal of this project is to use sqoop to load data from mysql to HBase



# Steps

1. install mysql in cloudera VM, default mysql password is cloudera
— sudo yum install mysql-server 
— pip install pymysql
— sudo /usr/bin/mysql_secure_installation

2. connect to database
— mysql -u root -p

3. create test database and grant privileges
— create database hbase CHARACTER；
- GRANT ALL PRIVILEGES ON hbase.* TO 'root'@'%' IDENTIFIED BY '1' WITH GRANT OPTION;  (here '1' is the password I set)

4. create test table
— use hbase
and run the commands from create _table_hly_temp_normal.txt to create tables

5. load data to mysql
modify the database name, username, password in insert_hly.py to match yours
— python insert_hly.py -f hly-temp-normal.txt -t hly_temp_normal

6. verify if data is loaded successfully
— select count(*) from hly_temp_normal;

7. install sqoop (1.4.6 version), modify the config file sqoop -env.sh, and set the below three directories
— export HADOOP_COMMON_HOME=' '
— export HADOOP_MAPRED_HOME=' '
— export HBASE_HOME=' '

8. copy the jdbc driver to the lib directory of sqoop

9. Test if sqoop work
— ./sqoop help

10. connect to mysql and see available databases
— ./sqoop list-databases --connect jdbc:mysql://localhost:3306/ -username root -password 1

11. create test table in HBase
— create 'hly_temp', {NAME => 'cf1', VERSIONS => 1}

12. load data from mysql to HBase
— ./sqoop import --connect jdbc:mysql://localhost:3306/hbase --username root -password 1 --table hly_temp_normal --hbase-table hly_temp --column-family cf1 --hbase-row-key id -m 1