package com.sss.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 Thread-0生产：10
 Thread-0生产：10
 Thread-1消费：10
 生产者生产到10个并打印；然后消费者消费完（剩9个）但执行输出语句的时候切换了线程，生产者又生产了一个到10个且打印了出来；然后切换会消费者，执行未执行的输出语句（并未消费），导致又输出一次10个。
 想要解决就要加锁保证线程安全。（BlockingQueue是线程安全的，但是这几行代码并没有保证线程安全。）
 */

public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }
}

class Producer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}