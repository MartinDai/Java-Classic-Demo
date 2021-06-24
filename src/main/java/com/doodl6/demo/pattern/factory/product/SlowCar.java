package com.doodl6.demo.pattern.factory.product;

public class SlowCar extends Car {

    @Override
    public void run() {
        System.out.println("pu pu pu");
    }
}