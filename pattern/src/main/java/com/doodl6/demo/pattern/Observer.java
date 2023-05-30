package com.doodl6.demo.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式
 */
public class Observer {

    protected List<MarryListener> listeners = new ArrayList<>();

    public void addListener(MarryListener listener) {
        listeners.add(listener);
    }

    public void notifyMarry(String classmateName) {
        for (MarryListener listener : listeners) {
            listener.notify(classmateName);
        }
    }

    public interface MarryListener {
        void notify(String classmateName);
    }

    public static class Classmate implements MarryListener {

        public String name;

        public Classmate(String name) {
            this.name = name;
        }

        @Override
        public void notify(String classmateName) {
            System.out.println(name + "收到了同学" + classmateName + "的结婚通知！");
        }
    }

    public static void main(String[] args) {
        Classmate m1 = new Classmate("小王");
        Classmate m2 = new Classmate("小李");
        Observer observer = new Observer();
        observer.addListener(m1);
        observer.addListener(m2);

        observer.notifyMarry("小花");
    }
}
