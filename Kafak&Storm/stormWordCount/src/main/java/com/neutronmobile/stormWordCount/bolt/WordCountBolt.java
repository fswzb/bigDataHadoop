package com.neutronmobile.stormWordCount.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.neutronmobile.stormWordCount.util.MapSort;

import java.util.HashMap;
import java.util.Map;

// count word and emit top N words based on frequency in real time
@SuppressWarnings("serial")
public class WordCountBolt implements IRichBolt {
	// Define a global Map to store the relations between word and frequency 
	Map<String, Integer> counters;
    private OutputCollector outputCollector;

    @SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        outputCollector = collector;
        counters = new HashMap<String, Integer>();
    }

	public void execute(Tuple input) {
        String str = input.getString(0);
        // if word is not in the Map, then set the frequency of this word to be 1
        if (!counters.containsKey(str)) {
            counters.put(str, 1);
        } 
        // if word is already in the Map, then increment the word frequency by 1
        else 
        {
            Integer c = counters.get(str) + 1;
            counters.put(str, c);
        }

        // we only want top 8 highest frequent words (we can change this number as we want)
        int num = 8;
        int length = 0;

        // use MapSort to do sorting for Map, based on value, so counter will be sorted in descending order
        counters = MapSort.sortByValue(counters);
        // if the size of Map is greater than num, then we will use top N, if not, then we use all the value from Map
        if (num < counters.keySet().size()) {
            length = num;
        } else {
            length = counters.keySet().size();
        }

        String word = null;

        int count = 0;
        // get all the key via KeySet, and get corresponding value by key
        for (String key : counters.keySet()) {

            //get top N, and jump out of the loop
            if (count >= length) {
                break;
            }
            
            if (count == 0) {
                word = "[" + key + ":" + counters.get(key) + "]";
            } else {
                word = word + ", [" + key + ":" + counters.get(key) + "]";
            }
            count++;
        }
        // use top N words' key and value to form a string, and emit it
        word = "The first " + num + ": "+ word;
        outputCollector.emit(new Values(word));

    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
