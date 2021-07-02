package com.doodl6.demo.schedule.timewheel;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 简单版时间轮，核心代码来自于netty
 */
public class SimpleWheelTimer {

    /**
     * 对字段做原子操作的修改类
     */
    private static final AtomicIntegerFieldUpdater<SimpleWheelTimer> WORKER_STATE_UPDATER;

    static {
        WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SimpleWheelTimer.class, "workerState");
    }

    /**
     * 每一轮的周期，单位：纳秒
     */
    private final long tickDuration;

    private final WheelBucket[] wheel;
    private final int mask;

    private final Worker worker = new Worker();
    private final Thread workerThread;

    /**
     * 工作状态，0-初始化，1-已启动，2-已停止
     */
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private volatile int workerState = WORKER_STATE_INIT; // 0 - init, 1 - started, 2 - shut down

    /**
     * 开始时间，单位：纳秒，worker启动时初始化，之后保持不变
     */
    private volatile long startTime;
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);

    private final Queue<WheelTimeout> timeouts = new LinkedBlockingQueue<>();

    public SimpleWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
        }
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }

        // 保证wheel数组的大小是2的次方
        wheel = createWheel(ticksPerWheel);
        mask = wheel.length - 1;

        // 转换周期单位为纳秒
        this.tickDuration = unit.toNanos(tickDuration);

        // 处理周期有效性
        if (this.tickDuration >= Long.MAX_VALUE / wheel.length) {
            throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", tickDuration, Long.MAX_VALUE / wheel.length));
        }
        workerThread = threadFactory.newThread(worker);
    }

    /**
     * 初始化时间轮的槽数组
     */
    private static WheelBucket[] createWheel(int ticksPerWheel) {
        if (ticksPerWheel <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
        }
        if (ticksPerWheel > 1073741824) {
            throw new IllegalArgumentException("ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
        }

        ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
        WheelBucket[] wheel = new WheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; i++) {
            wheel[i] = new WheelBucket();
        }
        return wheel;
    }

    /**
     * 返回大于ticksPerWheel的最小2次方数
     */
    private static int normalizeTicksPerWheel(int ticksPerWheel) {
        int normalizedTicksPerWheel = 1;
        while (normalizedTicksPerWheel < ticksPerWheel) {
            normalizedTicksPerWheel <<= 1;
        }
        return normalizedTicksPerWheel;
    }

    /**
     * 启动时间轮
     */
    public void start() {
        switch (WORKER_STATE_UPDATER.get(this)) {
            case WORKER_STATE_INIT:
                if (WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
                    workerThread.start();
                }
                break;
            case WORKER_STATE_STARTED:
                break;
            case WORKER_STATE_SHUTDOWN:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }

        // 等待worker线程启动完成
        while (startTime == 0) {
            try {
                startTimeInitialized.await();
            } catch (InterruptedException ignore) {
                // Ignore - it will be ready very soon.
            }
        }
    }

    /**
     * 新增一个超时任务
     */
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        start();

        // 计算到期时间，单位：纳秒，添加到超时任务队列，时间轮每个周期都会从队列中转移超时任务到槽的链表中
        long deadline = System.nanoTime() + unit.toNanos(delay) - startTime;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() + unit.toMillis(delay));
        WheelTimeout timeout = new WheelTimeout(task, deadline, timestamp);
        timeouts.add(timeout);
        return timeout;
    }

    /**
     * 停止时间轮，返回未处理的超时任务
     */
    public Set<WheelTimeout> stop() {
        if (Thread.currentThread() == workerThread) {
            throw new IllegalStateException(SimpleWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
        }

        if (!WORKER_STATE_UPDATER.compareAndSet(this, WORKER_STATE_STARTED, WORKER_STATE_SHUTDOWN)) {
            // workerState can be 0 or 2 at this moment - let it always be 2.
            WORKER_STATE_UPDATER.set(this, WORKER_STATE_SHUTDOWN);

            return Collections.emptySet();
        }

        //等待worker线程结束
        boolean interrupted = false;
        while (workerThread.isAlive()) {
            workerThread.interrupt();
            try {
                workerThread.join(100);
            } catch (InterruptedException ignored) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }

        return worker.unprocessedTimeouts();
    }

    private final class Worker implements Runnable {
        private final Set<WheelTimeout> unprocessedTimeouts = new HashSet<>();

        /**
         * 已经过去的周期数
         */
        private long tick;

        @Override
        public void run() {
            // 初始化startTime.
            startTime = System.nanoTime();
            if (startTime == 0) {
                // 因为其他地方会根据startTime是否等于0判断worker是否启动成功，所以需要设置一个非0的值
                startTime = 1;
            }

            //通知在start()内等待的其他线程
            startTimeInitialized.countDown();

            do {
                //只要worker没有停止，则一直循环等待下个周期，并触发相关操作
                final long deadline = waitForNextTick();
                if (deadline > 0) {
                    //计算得到本周期需要处理任务的槽下标
                    int idx = (int) (tick & mask);
                    WheelBucket bucket = wheel[idx];
                    transferTimeoutsToBuckets();
                    bucket.expireTimeouts(deadline);
                    tick++;
                }
            } while (WORKER_STATE_UPDATER.get(SimpleWheelTimer.this) == WORKER_STATE_STARTED);

            // worker停止以后，填充未处理的超时任务
            for (WheelBucket bucket : wheel) {
                bucket.clearTimeouts(unprocessedTimeouts);
            }
            for (; ; ) {
                WheelTimeout timeout = timeouts.poll();
                if (timeout == null) {
                    break;
                }
                unprocessedTimeouts.add(timeout);
            }
        }

        /**
         * 转移添加到队列但是还没有加入到槽中的任务
         */
        private void transferTimeoutsToBuckets() {
            // 一次最多转移10万个任务，防止影响任务调度
            for (int i = 0; i < 100000; i++) {
                WheelTimeout timeout = timeouts.poll();
                if (timeout == null) {
                    //没有新加的任务
                    break;
                }

                //计算需要经过多少个周期才到期
                long calculated = timeout.deadline / tickDuration;
                //计算任务触发执行需要经过时间轮的轮数，每遍历完bucket数组一圈表示一轮
                timeout.remainingRounds = (calculated - tick) / wheel.length;

                //保证任务的触发周期不是过去的，不然会触发不到，如果小于当前周期，则直接放在当前周期的槽内，本方法执行完以后，会直接触发执行
                final long ticks = Math.max(calculated, tick);
                int stopIndex = (int) (ticks & mask);

                WheelBucket bucket = wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }

        /**
         * 计算并等待下一个周期的的到达
         */
        private long waitForNextTick() {
            // 下一个周期距离开始时间的间隔，单位：纳秒
            long deadline = tickDuration * (tick + 1);

            for (; ; ) {
                //当前时间距离开始时间的间隔，currentTime是一直增长的
                final long currentTime = System.nanoTime() - startTime;
                // 计算到下一个周期剩余的时间，单位：毫秒
                long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;

                //如果sleepTimeMs小于等于0，则表示已经到了下一个周期的时间
                if (sleepTimeMs <= 0) {
                    if (currentTime == Long.MIN_VALUE) {
                        //这属于异常情况了，返回负数，外层会跳过，进入下一个周期的等待
                        return -Long.MAX_VALUE;
                    } else {
                        //返回当前时间距离开始时间的间隔，外层会进入触发执行该周期内到期的任务
                        return currentTime;
                    }
                }

                try {
                    //休眠剩余的时间，如果被打断，则需要判断worker是不是已经停止了
                    Thread.sleep(sleepTimeMs);
                } catch (InterruptedException ignored) {
                    if (WORKER_STATE_UPDATER.get(SimpleWheelTimer.this) == WORKER_STATE_SHUTDOWN) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }

        /**
         * 获取未处理的超时任务
         */
        public Set<WheelTimeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet(unprocessedTimeouts);
        }
    }

    /**
     * 时间轮内部使用的超时任务类
     */
    private static final class WheelTimeout implements Timeout {

        /**
         * 简化了状态，只有初始化和已到期
         */
        private static final int ST_INIT = 0;
        private static final int ST_EXPIRED = 1;
        private static final AtomicIntegerFieldUpdater<WheelTimeout> STATE_UPDATER;

        static {
            STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(WheelTimeout.class, "state");
        }

        /**
         * 实际要执行的任务对象
         */
        private final TimerTask task;

        /**
         * 到期时间，单位：纳秒
         */
        private final long deadline;

        private final Timestamp timestamp;

        private volatile int state = ST_INIT;

        // 剩余轮数
        long remainingRounds;

        // 超时任务双向链表
        WheelTimeout next;
        WheelTimeout prev;

        // 超时任务所在的槽
        WheelBucket bucket;

        WheelTimeout(TimerTask task, long deadline, Timestamp timestamp) {
            this.task = task;
            this.deadline = deadline;
            this.timestamp = timestamp;
        }

        @Override
        public Timestamp getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean isExpired() {
            return state() == ST_EXPIRED;
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        public int state() {
            return state;
        }

        public void expire() {
            if (!compareAndSetState(ST_INIT, ST_EXPIRED)) {
                return;
            }

            try {
                task.run(this);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    /**
     * 时间轮的槽结构，内部维护一个双向链表
     */
    private static final class WheelBucket {
        private WheelTimeout head;
        private WheelTimeout tail;

        /**
         * 添加一个超时任务
         */
        public void addTimeout(WheelTimeout timeout) {
            assert timeout.bucket == null;
            timeout.bucket = this;
            if (head == null) {
                head = tail = timeout;
            } else {
                tail.next = timeout;
                timeout.prev = tail;
                tail = timeout;
            }
        }

        /**
         * 触发执行槽内所有满足指定过期时间的任务
         */
        public void expireTimeouts(long deadline) {
            WheelTimeout timeout = head;

            //遍历槽内的所有任务
            while (timeout != null) {
                boolean remove = false;
                //如果剩余的轮数小于等于0，表示已经到期，需要触发执行
                if (timeout.remainingRounds <= 0) {
                    if (timeout.deadline <= deadline) {
                        timeout.expire();
                    } else {
                        // 任务放错了槽，这应该不会发生
                        throw new IllegalStateException(String.format("timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    }
                    remove = true;
                } else {
                    //任务的轮数减1
                    timeout.remainingRounds--;
                }

                WheelTimeout next = timeout.next;
                //如果任务任务触发或执行，则需要删除
                if (remove) {
                    remove(timeout);
                }
                timeout = next;
            }
        }

        /**
         * 从当前槽删除一个任务，任务执行过以后会调用此方法
         */
        public void remove(WheelTimeout timeout) {
            WheelTimeout next = timeout.next;
            if (timeout.prev != null) {
                timeout.prev.next = next;
            }
            if (timeout.next != null) {
                timeout.next.prev = timeout.prev;
            }

            if (timeout == head) {
                //如果这个任务既是任务头也是任务尾，则表示该槽内只有这一个任务，则直接把任务头和任务尾设置为null
                if (timeout == tail) {
                    tail = null;
                    head = null;
                } else {
                    // 如果是任务头被删了，且槽内有多个任务，则更新任务头为该任务的下一个任务
                    head = next;
                }
            } else if (timeout == tail) {
                // 如果是任务尾被删了，且槽内有多个任务，则更新任务头为该任务的前一个任务
                tail = timeout.prev;
            }

            //设置各个关联属性为null，帮助GC
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
        }

        /**
         * 清空槽内所有的任务，并把还没到期的任务放入set中
         */
        public void clearTimeouts(Set<WheelTimeout> set) {
            for (; ; ) {
                WheelTimeout timeout = pollTimeout();
                if (timeout == null) {
                    return;
                }
                if (timeout.isExpired()) {
                    continue;
                }
                set.add(timeout);
            }
        }

        /**
         * 弹出任务头
         */
        private WheelTimeout pollTimeout() {
            WheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            WheelTimeout next = head.next;
            if (next == null) {
                tail = this.head = null;
            } else {
                this.head = next;
                next.prev = null;
            }

            // 属性设置为null，帮助GC
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }
    }

    static class MyTimerTask implements TimerTask {

        @Override
        public void run(Timeout timeout) throws Exception {
            System.out.println("计划执行时间：" + timeout.getTimestamp() + "  实际执行时间：" + new Timestamp(System.currentTimeMillis()));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleWheelTimer simpleWheelTimer = new SimpleWheelTimer(r -> {
            Thread thread = new Thread(r);
            thread.setName("SimpleWheelTimer");
            return thread;
        }, 1, TimeUnit.MILLISECONDS, 128);

        simpleWheelTimer.newTimeout(new MyTimerTask(), 5121, TimeUnit.MILLISECONDS);
        simpleWheelTimer.newTimeout(new MyTimerTask(), 2444, TimeUnit.MILLISECONDS);
        simpleWheelTimer.newTimeout(new MyTimerTask(), 1232, TimeUnit.MILLISECONDS);
        simpleWheelTimer.newTimeout(new MyTimerTask(), 4222, TimeUnit.MILLISECONDS);
        simpleWheelTimer.newTimeout(new MyTimerTask(), 3533, TimeUnit.MILLISECONDS);

        Thread.sleep(10000);
        simpleWheelTimer.stop();
    }
}




