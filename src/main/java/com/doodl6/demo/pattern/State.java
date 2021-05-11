package com.doodl6.demo.pattern;

/**
 * 状态模式
 */
public class State {

    private int state = 0;

    public void printState() {
        System.out.println("current state is " + state);
    }

    public void doSomething() {
        System.out.println("doSomething() invoked");
        state = 1;
    }

    public static void main(String[] args) {
        State state = new State();
        state.printState();
        state.doSomething();
        state.printState();
    }
}
