package com.hz.exchange;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class aaa {

    static String file = "C:\\Users\\hez\\Desktop\\c\\期望导入格式.txt";
    static String outFile = "C:\\Users\\hez\\Desktop\\c\\期望导入格式1.txt";
    public static void main(String[] args) throws Exception{
//        new aaa().test14();
        String message = "a,b,v";
        String[] split = message.split(",");
        System.out.println(split[4]);;

    }
    
    public void test14() throws IOException {
        // 输入和输出都不使用缓冲流
        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(outFile,true);
        int len = 0;
        byte[] bs = new byte[1024];
        long begin = System.currentTimeMillis();
        while ((len = in.read(bs)) != -1) {
            System.out.println(new String(bs));
            out.write(bs, 0, len);
            out.flush();
        }
        System.out.println("复制文件所需时间：" + (System.currentTimeMillis() - begin)); // 平均时间 700 多毫秒
        in.close();
        out.close();
    }

    public void  test13() throws IOException {
        // 只有输入使用缓冲流
        FileInputStream in = new FileInputStream(file);
        BufferedInputStream inBuffer = new BufferedInputStream(in);
        FileOutputStream out = new FileOutputStream(outFile);
        int len = 0;
        byte[] bs = new byte[1024];
        long begin = System.currentTimeMillis();
        while ((len = inBuffer.read(bs)) != -1) {
            out.write(bs, 0, len);
        }
        System.out.println("复制文件所需时间：" + (System.currentTimeMillis() - begin)); // 平均时间约 500 多毫秒
        inBuffer.close();
        in.close();
        out.close();
    }
}
