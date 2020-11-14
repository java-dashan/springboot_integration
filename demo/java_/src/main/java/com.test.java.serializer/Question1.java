package com.test.java.serializer;


public class Question1 {

    /**
     * move all positive numbers to the left, and move all negative numbers to the right
     *
     * @param args
     */
    public static void main(String[] args) {

        int ints[] = new int[]{6, 4, -3, 5, -2, -1, 0, 1, -9};
        int[] parse = parse(ints);
        for (int i = 0; i < parse.length; i++) {
            System.out.println(parse[i]);
        }
    }

    public static int[] parse(int[] arr) {
        int[] ints = new int[arr.length];

        int rightIndex = arr.length - 1;
        int leftIndex = 0;

        for (int i = 0; i < arr.length; i++) {
            int value = arr[i];
            if (value < 0) {
                ints[rightIndex--] = value;
            } else if (value > 0) {
                ints[leftIndex++] = value;
            }
        }
//        数组默认初始化为0, 该步骤可以省略
//        for (int i = leftIndex; i <= rightIndex; i++) {
//            ints[leftIndex] = 0;
//        }
        return ints;
    }
}
