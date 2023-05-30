package com.doodl6.demo.pattern.adapter;

/**
 * 适配器模式(接口适配器)
 */
public class InterfaceAdapter extends Adaptee implements Target {

    @Override
    public void request() {
        adapteeMethod();
    }

    public static void main(String[] args) {
        Target target = new InterfaceAdapter();
        target.request();
    }
}
