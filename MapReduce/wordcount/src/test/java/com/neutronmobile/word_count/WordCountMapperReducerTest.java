package com.neutronmobile.word_count;

import com.neutronmobile.word_count.WordCount.IntSumReducer;
import com.neutronmobile.word_count.WordCount.TokenizerMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class WordCountMapperReducerTest {

	MapDriver<Object, Text, Text, IntWritable> mapDriver;
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
	MapReduceDriver<Object, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

	@Before
	public void setUp() {
		TokenizerMapper mapper = new TokenizerMapper();
		IntSumReducer reducer = new IntSumReducer();

		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);

		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.withInput(new LongWritable(), new Text("Hadoop is cool, very cool"));
		mapDriver.withOutput(new Text("Hadoop"), new IntWritable(1));
		mapDriver.withOutput(new Text("is"), new IntWritable(1));
		mapDriver.withOutput(new Text("cool,"), new IntWritable(1));
		mapDriver.withOutput(new Text("very"), new IntWritable(1));
		mapDriver.withOutput(new Text("cool"), new IntWritable(1));
		mapDriver.runTest();
	}

	@Test
	public void testReducer() throws IOException {
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		reduceDriver.withInput(new Text("Hadoop"), values);
		reduceDriver.withOutput(new Text("Hadoop"), new IntWritable(2));
		
		reduceDriver.runTest();
	}

}
