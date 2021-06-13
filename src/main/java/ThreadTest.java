import org.junit.Test;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author afei
 * @date:2021/6/9
 */
public class ThreadTest {
    //1
    public static void main(String[] args) {
        final Exchanger<Object> exchanger = new Exchanger<>();//Object为两个线程交换的数据类型
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "开始启动....");
                try {
                    Object object = new Object();
                    System.out.println(Thread.currentThread().getName() + " Thread-A send Object: " + object);
                    Object obj = exchanger.exchange(object);
                    System.out.println(Thread.currentThread().getName() + " Thread-A get Object: " + obj);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, "Thread-A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "开始启动....");
                try {
                    TimeUnit.SECONDS.sleep(5);
                    Object object = new Object();
                    System.out.println(Thread.currentThread().getName() + " Thread-B send Object: " + object);
                    Object obj = exchanger.exchange(object);
                    System.out.println(Thread.currentThread().getName() + " Thread-B get Object: " + obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, "Thread-B").start();
    }

    //2、测试ReentrantLock的公平锁和非公平锁
    @Test
    public void test1() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ReentrantLock fairLock = new ReentrantLock(true);
        ReentrantLock unFairLock = new ReentrantLock();
        for (int i = 0; i < 10; i++) {
            threadPool.submit(new TestThread(fairLock, i, " fairLock"));
            threadPool.submit(new TestThread(unFairLock, i, "unFairLock"));
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class TestThread implements Runnable {
        Lock lock;
        int indext;
        String tag;

        public TestThread(Lock lock, int index, String tag) {
            this.lock = lock;
            this.indext = index;
            this.tag = tag;
        }

        @Override
        public void run() {
            //System.out.println(indext + " 号线程 START  " + tag );
            meath();
        }

        private void meath() {
            lock.lock();
            try {
                if ((indext & 0x1) == 1) {
                    Thread.sleep(200);
                } else {
                    Thread.sleep(500);
                }
                System.out.println(tag+indext + " 号线程 获得： Lock  ---》" );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    //3、测试synchronized是非公平锁
    @Test
    public void synchronizedTest() {
        for(int i=0;i<20;i++){
            int finalI = i;
            new Thread(() ->
                    test(finalI)
            ).start();
        }
    }
    synchronized  private void test(int index) {
        System.out.println("--------------- > Task :" + index);
    }

    //4、测试交叉打印
    @Test
    public  void test4(){
        //for (int i = 0; i < 1000000; i++) {
            ThreadA threadA = new ThreadA(); //打印数字
            ThreadB threadB = new ThreadB(); //打印字母
            threadA.setThread(threadB);
            threadB.setThread(threadA);
            //线程B先park 必须先start，否则的话，如果遍历的次数尽可能的大，你会发现程序不会正常结束，线程B处于wating状态
            //threadB.start();
            threadA.start();
            threadB.start();
            try {
                threadA.join();
                threadB.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println();
       // }
        System.out.println("process is over");
    }

    class ThreadA extends Thread {
    private Thread thread;

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        int count = 0;
        for (int i = 0; i < 52; i++) {
            System.out.print(i + 1);
            count++;
            if (count == 2) {
                count = 0;
                LockSupport.unpark(thread);//把许可给thread，此处thread为threadB
                LockSupport.park();//等待许可
            }
        }
    }
}

    class ThreadB extends Thread {
    private Thread thread;

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        for (int i = 0; i < 26; i++) {
            LockSupport.park();//等待许可
            System.out.print((char)(97 + i));
            LockSupport.unpark(thread);//把许可给thread，此处thread为threadA
        }
    }
}

}
