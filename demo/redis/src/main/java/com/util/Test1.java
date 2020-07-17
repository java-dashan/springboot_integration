package com.util;

public class Test1 {

    static String aaa;

    String ddd;

    public void bbb(){
        this.aaa = "11";
        System.out.println(Test1.aaa);
    }

    public static void ccc(){
        //错误，静态方法不能引用动态属性 和 this
//        this.aaa = "11";
        System.out.println(Test1.aaa);
    }

    public static void main(String[] args) {
        new Test1().bbb();

    }
}
