package com.test.java.serializer;

import java.util.*;

public class Question2 {
    public static void main(String[] args) {
        //        { ‘A’: 1, ‘B.A’: 2, ‘B.B’: 3, ‘CC.D.E’: 4, ‘CC.D.F’: 5}
//      初始化成指定格式的map
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B.A", 2);
        map.put("B.B", 3);
        map.put("CC.D.E", 4);
        map.put("CC.D.F", 5);


        HashMap<String, Object> result = new HashMap<>();

        HashMap<Integer, ArrayList<String>> stringHashMap = new HashMap<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
            String key = stringIntegerEntry.getKey();
            String[] split = key.split("\\.");
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                strings.add(split[i]);
            }
            stringHashMap.put(stringIntegerEntry.getValue(), strings);
        }

        System.out.println(stringHashMap);

        int max_layer = 0;
        for (Map.Entry<Integer, ArrayList<String>> integerArrayListEntry : stringHashMap.entrySet()) {
            int value = integerArrayListEntry.getValue().size();
            if (value > max_layer) {
                max_layer = value;
            }
        }
        int current_layer = 0;
        HashMap[] maps = new HashMap[max_layer];
        for (int i = 0; i < max_layer; i++) {
            for (Map.Entry<Integer, ArrayList<String>> integerArrayListEntry : stringHashMap.entrySet()) {
                ArrayList<String> value = integerArrayListEntry.getValue();
                int size = value.size();
                if (i  < size) {
                    if (maps[i] == null) {
                        maps[i] = new HashMap();
                    }
                    HashMap map1 = maps[i];
                    if (i + 1 == size) {
                        map1.put(value.get(i), integerArrayListEntry.getKey());
                    } else if (i + 1 < size){
                        if (i + 2 < size) {
                            map1.put(value.get(i), integerArrayListEntry.getKey());
                        } else if (i + 2 == size) {
                            map1.put(value.get(i), new HashMap<>());
                        }
                    }
                }
            }
        }
        System.out.println(max_layer);

    }

    public static HashMap<Object, Object> parse(HashMap<Integer, ArrayList<String>> map, Integer layer, HashMap result) {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<String>> stringArrayListEntry : map.entrySet()) {
            ArrayList value = stringArrayListEntry.getValue();
            if (value.size() == layer) {
//                stringIntegerHashMap.put();
            } else {

            }
        }
        return result;
    }
}
