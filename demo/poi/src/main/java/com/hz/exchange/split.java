package com.hz.exchange;

import java.io.*;

public class split {
    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\hez\\Desktop\\c\\";
        File file = new File(path + "期望导入格式.txt");
        File newFile = new File(path + "期望导入格式1.txt");

        if (!file.exists()) {
            System.out.println("文件不存在");
            return;
        }

        FileInputStream input = new FileInputStream(file);
        BufferedInputStream bufinput = new BufferedInputStream(input);

        FileOutputStream output = new FileOutputStream(newFile,true);
        BufferedOutputStream bufoutput = new BufferedOutputStream(output);

        StringBuffer stringBuffer = new StringBuffer();

        int len = 0;

        byte[] bytes = new byte[1024 * 1024];

        while ((len = bufinput.read(bytes)) != -1) {
            String s = new String(bytes, 0, len);
            String s1 = s.replaceAll("\\t", ",");
            stringBuffer.append(s1);
        }

        System.out.println(stringBuffer);
        output.write(stringBuffer.toString().getBytes());
        output.flush();
        bufinput.close();
        input.close();
        bufoutput.close();
        output.close();

    }

    public void test(BufferedInputStream inputStream,byte [] bytes) throws Exception{
        int i = 0;
        while ((i = inputStream.read(bytes)) != -1) {

        }
    }
}
