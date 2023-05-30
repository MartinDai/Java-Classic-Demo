package com.doodl6.demo.pattern;

/**
 * 策略模式
 */
public class Strategy {

    private TravelStrategy strategy;

    public void setStrategy(TravelStrategy strategy) {
        this.strategy = strategy;
    }

    public void travel() {
        strategy.travel();
    }

    public interface TravelStrategy {
        void travel();
    }

    public static class CarStrategy implements TravelStrategy {

        @Override
        public void travel() {
            System.out.println("开车出行");
        }
    }

    public static class SubwayStrategy implements TravelStrategy {

        @Override
        public void travel() {
            System.out.println("地铁出行");
        }
    }

    public static void main(String[] args) {
        Strategy strategy = new Strategy();
        strategy.setStrategy(new CarStrategy());
        strategy.travel();

        strategy.setStrategy(new SubwayStrategy());
        strategy.travel();
    }
}
