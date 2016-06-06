'''
running step:

hadoop fs -copyFromLocal input.txt /user/cloudera/input.txt
hadoop jar target/word-count-1.0.0.jar com.neutronmobile.word_count.WordCount /user/cloudera/input.txt /user/cloudera/output
hadoop fs -cat /user/cloudera/output/*
'''

'''
[cloudera@quickstart wordcount]$ hadoop jar target/word-count-1.0.0.jar com.neutronmobile.word_count.WordCount /user/cloudera/input.txt /user/cloudera/output
16/06/05 14:19:20 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
16/06/05 14:19:21 WARN mapreduce.JobResourceUploader: Hadoop command-line option parsing not performed. Implement the Tool interface and execute your application with ToolRunner to remedy this.
16/06/05 14:19:21 INFO input.FileInputFormat: Total input paths to process : 1
16/06/05 14:19:21 INFO mapreduce.JobSubmitter: number of splits:1
16/06/05 14:19:21 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1465157443399_0001
16/06/05 14:19:22 INFO impl.YarnClientImpl: Submitted application application_1465157443399_0001
16/06/05 14:19:22 INFO mapreduce.Job: The url to track the job: http://quickstart.cloudera:8088/proxy/application_1465157443399_0001/
16/06/05 14:19:22 INFO mapreduce.Job: Running job: job_1465157443399_0001
16/06/05 14:19:34 INFO mapreduce.Job: Job job_1465157443399_0001 running in uber mode : false
16/06/05 14:19:34 INFO mapreduce.Job:  map 0% reduce 0%
16/06/05 14:19:43 INFO mapreduce.Job:  map 100% reduce 0%
16/06/05 14:19:50 INFO mapreduce.Job:  map 100% reduce 100%
16/06/05 14:19:51 INFO mapreduce.Job: Job job_1465157443399_0001 completed successfully
16/06/05 14:19:51 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=4145
		FILE: Number of bytes written=235575
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=3491
		HDFS: Number of bytes written=2921
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=6508
		Total time spent by all reduces in occupied slots (ms)=4744
		Total time spent by all map tasks (ms)=6508
		Total time spent by all reduce tasks (ms)=4744
		Total vcore-seconds taken by all map tasks=6508
		Total vcore-seconds taken by all reduce tasks=4744
		Total megabyte-seconds taken by all map tasks=6664192
		Total megabyte-seconds taken by all reduce tasks=4857856
	Map-Reduce Framework
		Map input records=6
		Map output records=541
		Map output bytes=5535
		Map output materialized bytes=4145
		Input split bytes=120
		Combine input records=541
		Combine output records=306
		Reduce input groups=306
		Reduce shuffle bytes=4145
		Reduce input records=306
		Reduce output records=306
		Spilled Records=612
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=98
		CPU time spent (ms)=1630
		Physical memory (bytes) snapshot=466059264
		Virtual memory (bytes) snapshot=3143208960
		Total committed heap usage (bytes)=391118848
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=3371
	File Output Format Counters 
		Bytes Written=2921

'''
