package com.doodl6.demo.pattern;

/**
 * 外观模式
 */
public class Facade {

    private final KitchenKnife knife = new KitchenKnife();
    private final Wok wok = new Wok();
    private final Plate plate = new Plate();

    public static class KitchenKnife {
        public void use() {
            System.out.println("用菜刀切菜");
        }
    }

    public static class Wok {
        public void use() {
            System.out.println("用炒锅炒菜");
        }
    }

    public static class Plate {
        public void use() {
            System.out.println("用盘子盛菜");
        }
    }

    public void cook() {
        System.out.println("开始做菜");
        knife.use();
        wok.use();
        plate.use();
        System.out.println("上菜");
    }

    public static void main(String[] args) {
        Facade facade = new Facade();
        facade.cook();
    }
}
