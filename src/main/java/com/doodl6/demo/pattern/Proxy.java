package com.doodl6.demo.pattern;

/**
 * 代理模式
 */
public class Proxy {

    private final Subject subject;

    public Proxy(Subject subject) {
        this.subject = subject;
    }

    public void doAction() {
        preAction();
        subject.doAction();
        postAction();
    }

    private void preAction() {
        System.out.println("preAction() invoked");
    }

    private void postAction() {
        System.out.println("postAction() invoked");
    }


    public static class Subject {

        public void doAction() {
            System.out.println("subject doAction() invoked");
        }
    }

    public static void main(String[] args) {
        Proxy proxy = new Proxy(new Subject());
        proxy.doAction();
    }
}
