package com.doodl6.demo.pattern;

/**
 * 装饰器模式
 */
public class Decorator implements Actionable {

    private final Actionable actionable;

    public Decorator(Actionable actionable) {
        this.actionable = actionable;
    }

    @Override
    public void action() {
        actionable.action();
        System.out.println("i can jump");
    }

    public static class Human implements Actionable {

        @Override
        public void action() {
            System.out.println("i can walk");
        }
    }

    public static void main(String[] args) {
        Actionable human = new Human();
        human.action();
        System.out.println("------------");
        Actionable decorator = new Decorator(human);
        decorator.action();
    }
}

interface Actionable {
    void action();
}
