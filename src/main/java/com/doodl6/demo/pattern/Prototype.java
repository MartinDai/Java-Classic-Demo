package com.doodl6.demo.pattern;

/**
 * 原型模式
 */
public class Prototype {

    private String name;

    private int age;

    public Prototype(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public Prototype clone() {
        return new Prototype(this.name, this.age);
    }

    public static void main(String[] args) {
        Prototype prototype1 = new Prototype("hello", 1);
        Prototype prototype2 = prototype1.clone();

        System.out.println(prototype1.getName().equals(prototype2.getName()));
        System.out.println(prototype1.getAge() == prototype2.getAge());
        System.out.println(prototype1 == prototype2);
    }
}
