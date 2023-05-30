package com.doodl6.demo.pattern.factory;

import com.doodl6.demo.pattern.factory.product.Car;
import com.doodl6.demo.pattern.factory.product.CarType;
import com.doodl6.demo.pattern.factory.product.FastCar;
import com.doodl6.demo.pattern.factory.product.SlowCar;

/**
 * 简单工厂模式
 */
public class SimpleFactory {

    public static Car buildCar(CarType carType) {
        switch (carType) {
            case FAST:
                return new FastCar();
            case SLOW:
                return new SlowCar();
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        Car fastCar = SimpleFactory.buildCar(CarType.FAST);
        fastCar.run();
        Car slowCar = SimpleFactory.buildCar(CarType.SLOW);
        slowCar.run();
    }
}
