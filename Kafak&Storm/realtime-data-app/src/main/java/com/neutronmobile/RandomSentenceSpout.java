package com.neutronmobile;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class RandomSentenceSpout extends BaseRichSpout {

	private static final long serialVersionUID = 7778072580517467871L;

  SpoutOutputCollector _collector;
  Random _rand;


  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
    _rand = new Random();
  }

  @Override
  public void nextTuple() {
    Utils.sleep(100);
    
    /*
     String[] sentences = new String[]{ 
    	"the cow jumped over the moon",
        "an apple a day keeps the doctor away",
        "four score and seven years ago", 
        "snow white and the seven dwarfs", 
        "i am at two with nature" };
      */
    
    /*
     String[] sentences = new String[]{ 
          	"google.com,visitors:500,download:100",
            "yahoo.com,visitors:500,download:100",
            "linkedin.com,visitors:300,download:10", 
            "neutronmobile.com,visitors:50,download:10", 
            "hortonworks.com,visitors:5,download:1" };
     */
    
    String[] sentences = new String[]{ 
          	"student-001,city:city1,state:CA",
            "student-002,city:city2,state:CA",
            "student-003,city:city3,state:CA", 
            "student-004,city:city4,state:CA", 
            "student-005,city:city5,state:CA" 
            };
   
    
    String sentence = sentences[_rand.nextInt(sentences.length)];
    _collector.emit(new Values(sentence));
  }

  @Override
  public void ack(Object id) {
  }

  @Override
  public void fail(Object id) {
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("word"));
  }
  
}