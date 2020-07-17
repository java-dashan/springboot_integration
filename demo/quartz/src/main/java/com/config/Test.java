package com.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    int i ;
    double d;
    char c;
    boolean b;
    Integer integer;
    int[] arr ;
    public static void main(String[] args) {
        ArrayList list = new ArrayList();
        list.add(1);
        list.add(1);
        list.add(1);
        System.out.println(1>>>5);
        System.out.println(16>>>3);
        System.out.println(16>>>4);
        System.out.println(16>>4);
        System.out.println(new Test().hashCode());
        System.out.println(new Test().hashCode());
        System.out.println(new Test().hashCode());
        System.out.println(new Test().arr);

        HashMap<Object, Object> map = new HashMap<>();
        map.put("a", 1);
    }
}
