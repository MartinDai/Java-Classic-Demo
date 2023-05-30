package com.doodl6.demo.pattern;

/**
 * 单例模式
 */
public class Singleton {

    private static final Singleton hungrySingleton = new Singleton();

    private static volatile Singleton lazySingleton;

    /**
     * 私有化构造函数
     */
    private Singleton() {
    }

    /**
     * 饿汉模式
     */
    public static Singleton getHungrySingleton() {
        return hungrySingleton;
    }

    /**
     * 懒汉模式
     */
    public static Singleton getLazySingleton() {
        if (lazySingleton == null) {
            synchronized (Singleton.class) {
                if (lazySingleton == null) {
                    lazySingleton = new Singleton();
                }
            }
        }
        return lazySingleton;
    }

    /**
     * 枚举方式
     */
    public static Singleton getInstance() {
        return SingletonEnum.INSTANCE.getInstance();
    }

    enum SingletonEnum {
        INSTANCE;
        private final Singleton singleton;

        SingletonEnum() {
            singleton = new Singleton();
        }

        public Singleton getInstance() {
            return singleton;
        }
    }

    public static void main(String[] args) {
        Singleton hungrySingleton1 = Singleton.getHungrySingleton();
        Singleton hungrySingleton2 = Singleton.getHungrySingleton();
        System.out.println(hungrySingleton1 == hungrySingleton2);

        Singleton lazySingleton1 = Singleton.getLazySingleton();
        Singleton lazySingleton2 = Singleton.getLazySingleton();
        System.out.println(lazySingleton1 == lazySingleton2);

        Singleton singleton1 = Singleton.getInstance();
        Singleton singleton2 = Singleton.getInstance();
        System.out.println(singleton1 == singleton2);
    }
}
