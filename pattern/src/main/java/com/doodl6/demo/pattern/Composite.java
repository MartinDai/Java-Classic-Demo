package com.doodl6.demo.pattern;

/**
 * 组合模式
 */
public class Composite {

    private final Bird bird;

    private final Fish fish;

    public Composite(Bird bird, Fish fish) {
        this.bird = bird;
        this.fish = fish;
    }

    public void fly() {
        bird.fly();
    }

    public void swim() {
        fish.swim();
    }

    public void run() {
        System.out.println("i can run");
    }

    public static class Bird {
        public void fly() {
            System.out.println("i can fly");
        }
    }

    public static class Fish {
        public void swim() {
            System.out.println("i can swim");
        }
    }

    public static void main(String[] args) {
        Bird bird = new Bird();
        Fish fish = new Fish();

        Composite composite = new Composite(bird, fish);
        composite.fly();
        composite.swim();
        composite.run();
    }
}
