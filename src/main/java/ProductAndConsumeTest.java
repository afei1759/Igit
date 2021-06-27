import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 模拟多个生产者、消费者
 * @Author afei
 * @date:2021/6/27
 */
public class ProductAndConsumeTest {
   public static class Source{
       private boolean hasValue=false;
       private Lock rlock=new ReentrantLock();
       private Condition pCondition=rlock.newCondition(); //生产者条件等待
       private Condition cCondition=rlock.newCondition();//消费者条件等待

       //模拟生产者的生产过程
       public void set(){
           try{
               //得到“rlock.lock()”锁
               rlock.lock();
               //如果已存在资源则停止生产
               //pCondition.await()会临时释放“rlock.lock()”锁，等signalAll唤醒后再次获取到“rlock.lock()”锁
               while(hasValue) pCondition.await();
               System.out.println(Thread.currentThread().getName()+"生产了一个值");
               hasValue=true;
               cCondition.signalAll();//唤醒所有因cCondition.await()而阻塞的消费者
           }catch (InterruptedException e) {
               e.printStackTrace();
           }finally {
               //释放“rlock.lock()”锁
               rlock.unlock();
           }

       }
       //模拟消费者消费的过程
       public void get(){
           try{
               //得到“rlock.lock()”锁
               rlock.lock();
               //如果不存在资源则停止消费
               //cCondition.await()会临时释放“rlock.lock()”锁，等signalAll唤醒后再次获取到“rlock.lock()”锁
               while(!hasValue) cCondition.await();
               System.out.println(Thread.currentThread().getName()+"消费了一个值");
               hasValue=false;
               pCondition.signalAll();//唤醒所有因pCondition.await()而阻塞的生产者
           }catch (InterruptedException e) {
               e.printStackTrace();
           }finally {
               //释放“rlock.lock()”锁
               rlock.unlock();
           }

       }
   }

    public static void main(String[] args){
        Source s=new Source();
        for(int i=0;i<10;i++){
            //5个生产者：奇数
            if(i%2==1)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        s.set();
                    }
                }).start();
            //5个消费者：偶数
            else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        s.get();
                    }
                }).start();
        }
    }
}
