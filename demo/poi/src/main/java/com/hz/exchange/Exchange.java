package com.hz.exchange;

import java.io.*;

public class Exchange {

//    private static String inputFile = "C:\\Users\\hez\\Desktop\\c\\期望导入格式1.txt";
//    private static String outFile = "C:\\Users\\hez\\Desktop\\c\\snp_imp_template.dat";
    public static void main(String[] args) throws Exception{
        String inPath = args[0];
        String outPath = args[1];
        System.out.println(inPath);
        System.out.println(outPath);
        File file = new File(inPath);
        File outFile = new File(outPath);
        if (!file.exists()) {
            return;
        }
        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        // 只有输入使用缓冲流
        FileInputStream in = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String s = bufferedReader.readLine();
        String s1 = s.replaceAll("\\t", ",");
        System.out.println(s);
        String[] title = s1.split(",");

        StringBuffer buffer = new StringBuffer();
        while (bufferedReader.ready()) {
            String message = bufferedReader.readLine();
            String s2 = message.replaceAll("\\t", ",");
            parse(buffer, s2, title);
        }
        //关闭输入流
        bufferedReader.close();
        inputStreamReader.close();
        in.close();

        FileWriter fileWriter = new FileWriter(outFile, true);
        fileWriter.write(buffer.toString());

        //关闭输出流
        fileWriter.close();

    }

    public static StringBuffer parse(StringBuffer buffer, String message,String [] arr) {
        String[] split = message.split(",");
        int length = message.length();
        String[] split2 = message.split(",");
        int index = length - ((split2.length - 6) * 2 - 1);
        String substring = message.substring(0,index);
//        System.out.println("subString:"+substring);
        String suffix = message.substring(index, length);
        String[] split1 = suffix.split(",");
        for (int i = 6; i < arr.length; i++) {
            if (i<(split1.length+6)) {
                buffer.append(arr[i]+",");
                buffer.append(substring);
                buffer.append(split1[i-6]);
                buffer.append("\n");
            }
        }
//        System.out.println(buffer);
        return buffer;
    }

    public void backUp() {
//        BufferedInputStream inBuffer = new BufferedInputStream(in);
//        BufferedReader bufferedReader = new BufferedReader(in);
//        FileOutputStream out = new FileOutputStream(outFile);
//
//        StringBuffer stringBuffer = new StringBuffer();
//        int len = 0;
//        byte[] bytes = new byte[1024];
//        while ((len = inBuffer.read(bytes)) != -1) {
//            String s = new String(bytes);
//        }
    }
}
