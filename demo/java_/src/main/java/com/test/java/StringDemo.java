package com.test.java;

import lombok.Data;
import sun.tools.javac.Main;

import java.io.PrintStream;

@Data
public class StringDemo {
    public static void main(String[] args) {
        String str1 = new StringBuilder("计算机").append("软件").toString();
//        String str1 = new String("计算机软件");
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);
//
//        String str3 = "ab";
//        String str4 = "java";
//        String str5 = new String("java");
//        System.out.println(str2.intern() == str3);
//        System.out.println(str3.intern() == str2);
//        System.out.println(str3 == str2);
//        System.out.println(str3 == str4);
//        System.out.println(str3 == str5.intern());
//
//        System.out.println("=================");
//        String s = new String("lun");
//        System.out.println(s.intern() == s);
//        System.out.println(s.intern() == "lun");
//
        StringDemo stringDemo = new StringDemo();
        stringDemo.test1();
        stringDemo.test2();
        stringDemo.test3();
        stringDemo.test4();

    }

    public void test1() {
        System.out.println("=================");
        char[] chars = new char[]{'a', 'b'};
        String s1 = new String(chars);
        System.out.println(s1.intern() == s1);
        String s2 = "ab";
        System.out.println(s1 == s2);
        System.out.println(s1.intern() == s1);
        System.out.println(s1 == s1);
        System.out.println(s1.intern() == s2);
    }

    public void test2() {
        System.out.println("=================");
        char[] chars = new char[]{'a', 'b'};
        String s1 = new String(chars);
        String s2 = "ab";
        System.out.println(s1 == s2);
        System.out.println(s1.intern() == s1);
        System.out.println(s1 == s2);
        System.out.println(s1.intern() == s1);
        System.out.println(s1.intern() == s2);
    }

    public void test3() {
        System.out.println("=================");
        String s1 = new String("ab");
        String s2 = "ab";
        System.out.println(s1.intern() == s1);
        System.out.println(s1 == s2);
        System.out.println(s1.intern() == s1);
        System.out.println(s1.intern() == s2);
    }

    public void test4() {
        System.out.println("=================");
//  new String("ab")    这个ab已经算是第一次出现了
        String s1 = new String("ab");
        System.out.println(s1.intern() == s1);
        String s2 = "ab";
        System.out.println(s1.intern() == s2);
    }
}
