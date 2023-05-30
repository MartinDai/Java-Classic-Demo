package com.doodl6.demo.pattern;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 解释器模式
 */
public class Interpreter {

    private final NumberExpression expression = new NumberExpression();

    public void plusNumber(String info) {
        Integer[] numberArray = expression.interpret(info);
        System.out.println("解析到的数字数组为：" + Arrays.stream(numberArray).map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("计算总和为：" + Arrays.stream(numberArray).reduce(Integer::sum).get());
    }

    public interface Expression {
        Integer[] interpret(String info);
    }

    public static class NumberExpression implements Expression {

        @Override
        public Integer[] interpret(String info) {
            return Arrays.stream(info.split(" ")).map(Integer::new).toArray(Integer[]::new);
        }
    }

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.plusNumber("111 333 44 5 6 7 8");
    }
}
