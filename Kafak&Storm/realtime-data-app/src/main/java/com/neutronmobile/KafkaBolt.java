package com.neutronmobile;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import kafka.producer.KeyedMessage;

public class KafkaBolt extends BaseRichBolt{
	private static final long serialVersionUID = -1497996985988288489L;
    private static final Logger LOG = Logger.getLogger(KafkaBolt.class);
    private Producer<String, String> producer;
    private String zkConnect, serializerClass, topic, metadataBrokerList, requestRequiredAcks;
    private OutputCollector collector;
        
    public KafkaBolt(Properties topologyConfig) {
        this.metadataBrokerList = topologyConfig.getProperty("metadata.broker.list");
        this.zkConnect = topologyConfig.getProperty("zk.connect");
        this.serializerClass = topologyConfig.getProperty("serializer.class");
        this.requestRequiredAcks = topologyConfig.getProperty("request.required.acks");
        this.topic = topologyConfig.getProperty("topic");
    }

    //@Override
    @SuppressWarnings("rawtypes")
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        Properties props = new Properties();
        props.put("metadata.broker.list", metadataBrokerList);
        props.put("zk.connect", zkConnect);
        props.put("serializer.class", serializerClass);
        props.put("request.required.acks", requestRequiredAcks);             
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    //@Override
    public void execute(Tuple tuple) {
        String line = tuple.getString(0);
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(this.topic, line);
        LOG.info("KAFKA BOLT: Next value to write to topic: " + data);
        producer.send(data);
        collector.ack(tuple);
    }

    //@Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("kafka_entry"));
                
    }      
}
