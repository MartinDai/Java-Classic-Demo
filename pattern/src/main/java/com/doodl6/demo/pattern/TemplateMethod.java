package com.doodl6.demo.pattern;

/**
 * 模板方法模式
 */
public class TemplateMethod {

    public static abstract class AbstractClass {

        public void doSomething() {
            System.out.println("start do something");
            abstractMethod1();
            abstractMethod2();
            System.out.println("do something finished");
        }

        public abstract void abstractMethod1();

        public abstract void abstractMethod2();
    }

    public static class RealClassA extends AbstractClass {

        @Override
        public void abstractMethod1() {
            System.out.println("RealClassA abstractMethod1() invoked");
        }

        @Override
        public void abstractMethod2() {
            System.out.println("RealClassA abstractMethod2() invoked");
        }
    }

    public static class RealClassB extends AbstractClass {

        @Override
        public void abstractMethod1() {
            System.out.println("RealClassB abstractMethod1() invoked");
        }

        @Override
        public void abstractMethod2() {
            System.out.println("RealClassB abstractMethod2() invoked");
        }
    }


    public static void main(String[] args) {
        AbstractClass ac = new RealClassA();
        ac.doSomething();
        System.out.println("--------------------");
        ac = new RealClassB();
        ac.doSomething();
    }
}
