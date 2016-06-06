package com.neutronmobile.stormWordCount;

import backtype.storm.tuple.Fields;
import com.neutronmobile.stormWordCount.bolt.PrintBolt;
import com.neutronmobile.stormWordCount.bolt.WordCountBolt;
import com.neutronmobile.stormWordCount.bolt.WordNormalizerBolt;
import com.neutronmobile.stormWordCount.spout.RandomSentenceSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

// start main class, create topology
public class WordCountTopology {
	// topology creator
	private static TopologyBuilder builder = new TopologyBuilder();

    public static void main(String[] args) {
    	// config declaration in main class
        Config config = new Config();
        // set number of tast to 2 for this sprout
        builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2);
        // randomly group and subscribe from above
        builder.setBolt("WordNormalizer", new WordNormalizerBolt(), 2).shuffleGrouping(
                "RandomSentence");
        builder.setBolt("WordCount", new WordCountBolt(), 2).fieldsGrouping("WordNormalizer",
                new Fields("word"));	
        builder.setBolt("Print", new PrintBolt(), 1).shuffleGrouping(
                "WordCount");

        config.setDebug(false);

        //control if run on cluster or local depending on the parameters
        if (args != null && args.length > 0) {
            try {
            	// cluster mode
                config.setNumWorkers(1);
                StormSubmitter.submitTopology(args[0], config,
                        builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	// local mode
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("wordcount", config, builder.createTopology());
        }
    }
}
