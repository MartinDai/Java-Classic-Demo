package com.doodl6.demo.pattern.factory;

import com.doodl6.demo.pattern.factory.product.Car;
import com.doodl6.demo.pattern.factory.product.FastCar;
import com.doodl6.demo.pattern.factory.product.SlowCar;

/**
 * 工厂方法模式
 */
public class FactoryMethod {

    interface CarFactory {
        Car buildCar();
    }

    static class SlowCarFactory implements CarFactory {
        @Override
        public Car buildCar() {
            return new SlowCar();
        }
    }

    static class FastCarFactory implements CarFactory {
        @Override
        public Car buildCar() {
            return new FastCar();
        }
    }

    public static void main(String[] args) {
        CarFactory carFactory = new SlowCarFactory();
        Car slowCar = carFactory.buildCar();
        slowCar.run();

        carFactory = new FastCarFactory();
        Car fastCar = carFactory.buildCar();
        fastCar.run();
    }
}
