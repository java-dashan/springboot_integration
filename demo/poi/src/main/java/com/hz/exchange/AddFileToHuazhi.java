package com.hz.exchange;

import java.io.*;
import java.util.Random;

public class AddFileToHuazhi {
    public static void main(String[] args) throws Exception{
        String path = "C:\\Users\\hez\\Desktop\\a\\snp_imp_template.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        String num1 = "LPGK";
        String num2 = "LP";
        int index = 1;

        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int k = 0; k < 100000; k++) {
            for (int i = 1; i <=10; i++) {
                for (int j = 1; j <= 10; j++) {
                    buffer.append(num1);
                    if (j == 10) {
                        buffer.append("0" + j + ",");
                    } else {
                        buffer.append("00" + j + ",");
                    }
                    buffer.append(num2);
                    String indexString = index + "";
                    switch (indexString.length()) {
                        case 1:
                            buffer.append("000" + indexString+",");
                            break;
                        case 2:
                            buffer.append("00" + indexString+",");
                            break;
                        case 3:
                            buffer.append("0" + indexString+",");
                            break;
                        case 4:
                            buffer.append(indexString+",");
                            break;
                        case 5:
                            buffer.append(6666+",");
                            break;
                    }
                    buffer.append((random.nextInt(8)+1)+",");
                    String that = (random.nextInt(10000000)+1000000)+",";
                    buffer.append(that);
                    buffer.append(that);
                    buffer.append(randomKey(random) + ",");
                    buffer.append(randomKey(random) + ",");
                    buffer.append(randomKey(random));
                    buffer.append("\n");
                }
            }
            index++;
        }
        System.out.println(buffer);

        fileOutputStream.write(buffer.toString().getBytes());

    }

    static String randomKey(Random random) {
        int i = random.nextInt(4);
        String arr[] = new String[]{"A","G","C","T","H"};
        return arr[i];
    }
}
