package com.aaa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B.A", 2);
        map.put("B.B", 3);
        map.put("CC.D.E", 4);
        map.put("CC.D.F", 5);
        HashMap<Integer, ArrayList<String>> stringHashMap = new HashMap<>();

        ArrayList<Object> objects = new ArrayList<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
            String key = stringIntegerEntry.getKey();
            String[] split = key.split("\\.");
            Node head = new Node();
            for (int i = split.length - 1; i >=0 ; i--) {
                if (i == split.length - 1) {
                    Node node = new Node();
                    HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
                    objectObjectHashMap.put(split[i], stringIntegerEntry.getValue());
                    node.next = objectObjectHashMap;
                    head.next = node;
                } else {
                    Node node = new Node();
                    node.next = head.next;
                    head.next = node;
                }
            }
            objects.add(head);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writeValueAsString(objects));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();

        for (int i = 0; i < objects.size(); i++) {

        }

        System.out.println(objectMapper.writeValueAsString(objectObjectHashMap));

    }
}
