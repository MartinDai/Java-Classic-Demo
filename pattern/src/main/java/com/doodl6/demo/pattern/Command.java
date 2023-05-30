package com.doodl6.demo.pattern;

/**
 * 命令模式
 */
public class Command {

    public enum CommandType {
        TURN_LEFT,
        TURN_RIGHT
    }

    public static class Invoker {

        public void execute(CommandType commandType) {
            if (commandType == null) {
                System.out.println("unknown command type");
                return;
            }
            switch (commandType) {
                case TURN_LEFT:
                    System.out.println("turn left");
                    break;
                case TURN_RIGHT:
                    System.out.println("turn right");
                    break;
                default:
                    System.out.println("not support");
            }
        }
    }

    public static void main(String[] args) {
        Invoker invoker = new Invoker();

        invoker.execute(CommandType.TURN_LEFT);
        invoker.execute(CommandType.TURN_RIGHT);
        invoker.execute(null);
    }
}
