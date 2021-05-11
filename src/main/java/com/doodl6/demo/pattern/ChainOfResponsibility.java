package com.doodl6.demo.pattern;

/**
 * 责任链模式
 */
public class ChainOfResponsibility {

    public abstract static class Handler {

        private final Handler next;

        public Handler(Handler next) {
            this.next = next;
        }

        public void doRequest() {
            preRequest();
            if (next != null) {
                next.doRequest();
            }
            postRequest();
        }

        protected abstract void preRequest();

        protected abstract void postRequest();
    }

    public static class FirstHandler extends Handler {

        public FirstHandler(Handler next) {
            super(next);
        }

        @Override
        public void preRequest() {
            System.out.println("first handler preRequest");
        }

        @Override
        protected void postRequest() {
            System.out.println("first handler postRequest");
        }
    }

    public static class SecondHandler extends Handler {

        public SecondHandler(Handler next) {
            super(next);
        }

        @Override
        public void preRequest() {
            System.out.println("second handler preRequest");
        }

        @Override
        protected void postRequest() {
            System.out.println("second handler postRequest");
        }
    }

    public static void main(String[] args) {
        Handler secondHandler = new SecondHandler(null);
        Handler firstHandler = new FirstHandler(secondHandler);

        firstHandler.doRequest();
    }
}
