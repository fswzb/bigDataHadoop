package com.neutronmobile.stormWordCount.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//util for map sorting
public class MapSort {
	// sort for map and length truncation
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// return type is Map，and method is sortByValue
	public static Map<String, Integer> sortByValue(Map<String, Integer> map) {

		if (map == null) {
			return null;
		}
		// create a double linked list
		List list = new LinkedList(map.entrySet());

		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				Comparable sort1 = (Comparable) ((Map.Entry) o1).getValue();
				Comparable sort2 = (Comparable) ((Map.Entry) o2).getValue();
				return sort2.compareTo(sort1);
			}

		});

		Map result = new LinkedHashMap();

		for (Iterator it = list.iterator(); it.hasNext();) {

			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());

		}

		return result;
	}

	public static void main(String[] args) {

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("test", 3);
		map.put("hcy", 1);
		map.put("put", 2);

		map = sortByValue(map);

		for (String key : map.keySet()) {
			System.out.println(key + " ==> " + map.get(key));
		}
	}
}
