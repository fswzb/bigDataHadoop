package com.neutronmobile.word_count;

import java.io.IOException;
// The string tokenizer class allows an application to break a string into tokens.
import java.util.StringTokenizer;
/* The hasMoreTokens() method is used to test if there are more 
tokens available from this tokenizer's string.
java.util.StringTokenizer.hasMoreTokens() Method
*/
/* The nextToken(String delim) method is used to return the next 
token in this string tokenizer's string.
java.util.StringTokenizer.nextToken() Method
*/

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

  /* By default, Key is LongWritable type and value is of type Text for the 
  TextInputFormat.In your example, Object type is specified in the place of 
  LongWritable as it is compatible. You can also use LongWritable type in the 
  place of Object */

  /* A mapper’s main work is to produce a list of key value pairs to be processed later.
  A mapper receives a key, value pair as parameters, produce a list of new key, value pairs.
  */

  /* The data types provided here are Hadoop specific data types designed for 
  operational efficiency suited for massive parallel and lightning fast read write 
  operations. All these data types are based out of java data types itself, 
  for example LongWritable is the equivalent for long in java, IntWritable for 
  int and Text for String.
  */
  public static class TokenizerMapper
  /*  In our example the input to a mapper is a single line, so this Text (one input line) 
  forms the input value. The input key would a long value assigned in default based 
  on the position of Text in input file. 
  Our output from the mapper is of the format “Word, 1“ hence the data type of our output 
  key value pair is <Text(String),  IntWritable(int)>
  */
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    /* map method that performs the tokenizer job and framing the initial 
    key value pairs */
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      /* iterating through all the words available in that line and 
      forming the key value pair */
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }
  /*
  The functionality of the map method is as follows
1.       Create a IntWritable variable ‘one’ with value as 1
2.       Convert the input line in Text type to a String
3.       Use a tokenizer to split the line into words
4.       Iterate through each word and a form key value pairs as
a.       Assign each work from the tokenizer(of String type) to a Text ‘word’
b.      Form key value pairs for each word as <word,one> and push it to the output collector
  */



  // reduce method accepts the Key Value pairs from mappers, do the aggregation 
  // based on keys and produce the final out put
  /* 
  Now the key component here, the reduce method.
  The input to reduce method from the mapper after the sort and shuffle phase would be 
  the key with the list of associated values with it. For example here we have multiple 
  values for a single key from our mapper like <apple,1> , <apple,1> , <apple,1> , 
  <apple,1> . This key values would be fed into the reducer as < apple, {1,1,1,1} > .

  Now let us evaluate our reduce method reduce(Text key, Iterator<IntWritable> values, 
  OutputCollector<Text, IntWritable> output, Reporter reporter)
  Here all the input parameters are hold the same functionality as that of a mapper, 
  the only diference is with the input Key Value. As mentioned earlier the input to a 
  reducer instance is a key and list of values hence  ‘Text key, Iterator<IntWritable> 
  values’ . The next parameter denotes the output collector of the reducer with the data 
  type of output Key and Value.
  */
  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      /*iterates through all the values available with a key and add them together 
      and give the final result as the key and sum of its values*/
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }
  /*
Reducer has three main phases:
1. Shuffle
2. Sort
3. Reduce
The first two phases are automatically taken care by the framework.
  
  The functionality of the reduce method is as follows
1.       Initaize a variable ‘sum’ as 0
2.       Iterate through all the values with respect to a key and sum up all of them
3.       Push to the output collector the Key and the obtained sum as value
  */


  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    // Create a new Job Configuration object and assigning a job 
    // name for identification purposes
    Job job = Job.getInstance(conf, "word count");
    // Set the Jar by finding where a given class came from.
    job.setJarByClass(WordCount.class);
    // specify a mapper
    job.setMapperClass(TokenizerMapper.class);
    // specify a reducer
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    // specify output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    // specify hdfs input and output dirs
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}