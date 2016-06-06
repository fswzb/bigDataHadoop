package com.neutronmobile;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class KafkaTopology extends BaseTopology
{
	//Topology components IDs
    private static final String KAFKA_BOLT_ID = "KafkaBolt";
    private static final String RANDOM_SENTENCE_SPOUT_ID = "RandomSentenceSpout";
    
    
    public KafkaTopology(String configFileLocation) throws Exception 
    {
        super(configFileLocation);
    }
    
    //Build and submit topology to the storm cluster
    @SuppressWarnings("unused")
	private void buildAndSubmit() throws Exception
    {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(RANDOM_SENTENCE_SPOUT_ID, new RandomSentenceSpout(), 1);
        
        builder.setBolt(KAFKA_BOLT_ID, new KafkaBolt(topologyConfig), 1).shuffleGrouping(RANDOM_SENTENCE_SPOUT_ID);
        
        Config conf = new Config(); 
        conf.setDebug(false);
        
        StormSubmitter.submitTopology("jason-kafka-storm", conf, builder.createTopology());
    }
    
    
    private void buildAndSubmitLocal() throws Exception
    {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(RANDOM_SENTENCE_SPOUT_ID, new RandomSentenceSpout(), 1);
        builder.setBolt(KAFKA_BOLT_ID, new KafkaBolt(topologyConfig), 1).shuffleGrouping(RANDOM_SENTENCE_SPOUT_ID);
        
        Config conf = new Config(); 
        conf.setDebug(true);
        
        conf.setMaxTaskParallelism(1);
		final LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("my-local-kafka-topology", conf, builder.createTopology());
    }

    public static void main(String[] str) throws Exception
    {
    	String configFileLocation = "topology-conf.properties";
    	KafkaTopology KafkaTopology  = new KafkaTopology(configFileLocation);
        //IF SUBMITTING THE TOPOLOGY TO THE STORM CLUSTER PLEASE CALL buildAndSubmit()
    	KafkaTopology.buildAndSubmit();
        //IF SUBMITTING THE TOPOLOGY TO LOCAL PLEASE CALL buildAndSubmitLocal()  
    	//KafkaTopology.buildAndSubmitLocal();
    	
    }

}
