package com.doodl6.demo.pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * 享元模式
 */
public class Flyweight {

    private final String name;

    public Flyweight(String name) {
        this.name = name;
    }

    public void print(String msg) {
        System.out.println("享元对象[" + name + "]打印信息：" + msg);
    }

    public static class FlyweightFactory {
        private static final Map<String, Flyweight> cache = new HashMap<>();

        public static Flyweight createFlyweight(String name) {
            Flyweight flyweight = cache.get(name);
            if (flyweight == null) {
                flyweight = new Flyweight(name);
                cache.put(name, flyweight);
                System.out.println("创建新的享元对象：" + name);
            } else {
                System.out.println("找到已经创建的享元对象：" + name);
            }

            return flyweight;
        }
    }

    public static void main(String[] args) {
        Flyweight flyweight1 = FlyweightFactory.createFlyweight("哈哈");
        Flyweight flyweight2 = FlyweightFactory.createFlyweight("哈哈");
        Flyweight flyweight3 = FlyweightFactory.createFlyweight("嘿嘿");

        flyweight1.print("1111");
        flyweight2.print("2222");
        flyweight3.print("3333");
    }

}
