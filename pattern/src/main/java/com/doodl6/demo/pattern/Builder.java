package com.doodl6.demo.pattern;

/**
 * 建造者模式
 */
public class Builder {

    protected Product product = new Product();

    public Builder partA(String partA) {
        product.setPartA(partA);
        return this;
    }

    public Builder partB(String partB) {
        product.setPartB(partB);
        return this;
    }

    public Builder partC(String partC) {
        product.setPartC(partC);
        return this;
    }

    public Product build() {
        return product;
    }

    public static class Product {

        private String partA;

        private String partB;

        private String partC;

        public String getPartA() {
            return partA;
        }

        public void setPartA(String partA) {
            this.partA = partA;
        }

        public String getPartB() {
            return partB;
        }

        public void setPartB(String partB) {
            this.partB = partB;
        }

        public String getPartC() {
            return partC;
        }

        public void setPartC(String partC) {
            this.partC = partC;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "partA='" + partA + '\'' +
                    ", partB='" + partB + '\'' +
                    ", partC='" + partC + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        Builder builder = new Builder();
        Product product = builder.partA("good")
//                .partB("nice")
                .partC("perfect")
                .build();

        System.out.println(product.toString());
    }
}
