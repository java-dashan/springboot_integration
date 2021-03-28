package com.test.java;

public class StaticDispatcherDemo {
    public static void main(String[] args) {
        class Father {
            public int money = 1;

            public Father() {
                money = 2;
                showMoney();
            }

            public void showMoney() {
                System.out.println("father " + money);
            }
        }
        class Son extends Father {
            public int money = 3;

            public Son() {
                money = 4;
                showMoney();
            }

            public void showMoney() {
                System.out.println("Son " + money);
            }
        }

        Father father = new Son();
        System.out.println("gay " + father.money);
        System.out.println("gay " + ((Son) father).money);
    }
}
