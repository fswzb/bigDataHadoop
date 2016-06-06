package com.neutronmobile.stormWordCount.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.Map;
import java.util.Random;

// randomly choose English sentences, and emit them as source
// this spout extends BaseRichSpout/IRichSpout class, BaseRichSpout is enough in this case since requirement is low
public class RandomSentenceSpout extends BaseRichSpout {
	// declare a SpoutOutputCollector object, and use it to emit messages
	SpoutOutputCollector spoutOutputCollector;
	// declare a random object, and use it to get random message
	Random random;

	// open method：some initialization for spout like passing parameters
	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		spoutOutputCollector = collector;
		random = new Random();
	}

	// methods to process tuples
	public void nextTuple() {
		// emitting every 2 seconds
		Utils.sleep(2000);
		String[] sentences = new String[] {
				"UCLA is a good school",
				"And if the golden sun",
				"four score and seven years ago",
				"storm hadoop spark hbase",
				"He is a good man",
				"our students study hard",
				"Would make my whole world bright",
				"Los Angeles is a good place",
				"storm would have to be with you",
				"Pipe to subprocess seems to be broken No output read",
				" You make me feel so happy",
				"For the moon never beams without bringing me dreams Of the beautiful Annalbel Lee",
				"Who love Beijing and Shanghai",
				"Today is a great day",
				"Ko linux swayed my leaves and flowers in the sun",
				"You love data engineer", "Now I may wither into the truth",
				"That the wind came out of the cloud",
				"at backtype storm utils ShellProcess",
				"Of those who were older than we" };
		// randomly choose one sentence from sentences array as the message for the spout to emit
		String sentence = sentences[random.nextInt(sentences.length)];
		// use emit method to send out tuples and use Values to declare parameters
		spoutOutputCollector.emit(new Values(sentence.trim().toLowerCase()));
	}

	// ack method in guaranteed message processing
	public void ack(Object id) {
	}

	// fail method in guaranteed message processing
	public void fail(Object id) {
	}

	// fields declaration
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}
