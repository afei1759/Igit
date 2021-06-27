import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @Description 测试解决CAS中的ABA问题
 * @Author afei
 * @date:2021/6/27
 */
public class AtomicStampedReferenceTest {
    //AtomicInteger和AtomicStampedReference的初始值都为100，但AtomicStampedReference带了版本号0
    private static AtomicInteger atomicInt = new AtomicInteger(100);
    private static AtomicStampedReference atomicStampedRef = new AtomicStampedReference(100, 0);
    public static void main(String[] args) {
        //会产生ABA问题
        aba1();
        //用AtomicStampedReference解决cas过程中的ABA问题
        aba2();
 }

 public static void aba1(){
     Thread t1 = new Thread(new Runnable() {
         @Override
         public void run() {
             boolean c3 = atomicInt.compareAndSet(100, 101);
             System.out.println(c3); // true
         }
     });
     Thread t2 = new Thread(new Runnable() {
         @Override
         public void run() {
             //t2改变100->101->100,compareAndSet的参数：期待的值，新值
             atomicInt.compareAndSet(100, 101);
             atomicInt.compareAndSet(101, 100);
         }
     });
     t1.start();
     t2.start();
     try {
         //t1,t2执行完，主线程才能继续执行
         t1.join();
         t2.join();
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }
  public static void aba2(){
      Thread t1 = new Thread(new Runnable() {
          @Override
          public void run() {
              int stamp = atomicStampedRef.getStamp();
              try {
                  TimeUnit.SECONDS.sleep(2);
              } catch (InterruptedException e) {
              }
              //compareAndSet中四个参数分别为：期待的值，新值，期待的版本号，新的版本号
              boolean c3 = atomicStampedRef.compareAndSet(100, 101, stamp, stamp + 1);
              System.out.println(c3); // false
          }
      });
      Thread t2 = new Thread(new Runnable() {
          @Override
          public void run(){
              try {
                  TimeUnit.SECONDS.sleep(1);
              } catch (InterruptedException e) {
              }
              //t2改变100->101->100，compareAndSet中四个参数分别为：期待的值，新值，期待的版本号，新的版本号
              atomicStampedRef.compareAndSet(100, 101, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
              atomicStampedRef.compareAndSet(101, 100, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
          }
      });
      t1.start();
      t2.start();
  }
}
