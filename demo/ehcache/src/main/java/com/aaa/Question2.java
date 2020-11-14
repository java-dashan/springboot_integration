package com.aaa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        int max_layer = 1;

        HashMap<Object, Object> parse = parse(stringHashMap, max_layer, result);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writeValueAsString(parse));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(max_layer);

    }

    public static HashMap<Object, Object> parse(HashMap<Integer, ArrayList<String>> map, Integer layer, HashMap result) {
        while (map.entrySet().size() > 0) {
            for (Map.Entry<Integer, ArrayList<String>> stringArrayListEntry : map.entrySet()) {
                ArrayList value = stringArrayListEntry.getValue();
                if (value.size() == layer) { // 初始为第一层
                    result.put(value.get(layer - 1), stringArrayListEntry.getKey());
                    map.remove(stringArrayListEntry.getKey());
                } else {
                    //                干掉当前层
                    if (value.size() - 1 != 0) {  //有上一层
                        HashMap hashMap = parsePre(result, value, 0, layer);
                        hashMap.put(value.get(layer), stringArrayListEntry.getKey());
                    } else { //代表没有上一层
                        result.put(value.get(layer), stringArrayListEntry.getKey());
                    }
                    map.remove(stringArrayListEntry.getKey());
                }
            }
        }
        return result;
    }

    public static HashMap parsePre(HashMap result, ArrayList list, int beginIndex, int layer) {
        HashMap hashMap = (HashMap) result.get(list.get(beginIndex));
        if ((beginIndex + 1) == layer) {
            return hashMap;
        } else {
            return parsePre(result, list, beginIndex + 1, layer);
        }
    }
}
