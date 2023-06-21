# Java-Classic-Demo

包含一些Java经典场景的使用案例

## file 

文件操作相关

- 使用不同方式执行文件复制操作

## javaagent

包含javaagent的开发demo

## jvm 

1. 模拟JVM运行时的一些错误场景

   - 直接内存溢出（java.lang.OutOfMemoryError: Direct buffer memory）
   - 堆内存溢出（java.lang.OutOfMemoryError: Heap space）
   - Metaspace溢出（java.lang.OutOfMemoryError: Metaspace）
   - 线程资源耗尽溢出（java.lang.OutOfMemoryError: unable to create new native）
   - 线程栈溢出（java.lang.StackOverflowError）

2. 自定义ClassLoader示例

## pattern

常见的17种设计模式简单示例

- 适配器模式
- 简单工厂模式
- 建造者模式
- 责任链模式
- 命令模式
- 组合模式
- 装饰器模式
- 外观模式
- 享元模式
- 解释器模式
- 观察者模式
- 原型模式
- 代理模式
- 单例模式
- 状态模式
- 策略模式
- 模板方法模式

## proxy

不同方式实现的动态代理

- Asm
- ByteBuddy
- Cglib
- Javassist
- JdkProxy

## schedule

定时调度相关

- 自定义简单版时间轮
- ScheduledThreadPoolExecutor
- Timer

## thread

多线程相关

- CountDownLatch的使用案例
- CyclicBarrier的使用案例
- Semaphore的使用案例
- Thread.join的使用案例

线程池相关

- 缓存线程（CachedThread）
- 固定线程（FixedThread）
- 常规线程（NormalThread）
- 单线程（SingleThread）