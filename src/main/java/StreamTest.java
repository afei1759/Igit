import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Description
 * @Author afei
 * @date:2021/4/27
 */
public class StreamTest {

    public static void main(String[] args){
        //用map和filter代替for循环换取可读性，是有效率代价的
        Optional<Integer> optional=Stream.of(1,2,3,4,5)
                .map(x->x*x)  //非终止操作：遍历，返回一个新的流数组Stream<Integer>，不改变原流数组
                .filter(x->x<10) //非终止操作：筛选，返回一个数组流Stream<Integer>
                .reduce(Math::max); //终止操作：数组按顺序流进'数据流管道'，再两两比较，找出数组流中最小的值并返回,用Optional包装后返回 Optional<Integer>。返回Optional<Integer>比返回Integer更安全，区别是若Stream流是空的，则Math.min()会传入两个空值比较，进而报异常，这个异常被Optional捕捉并封装，同时Optional返回一个空值
        System.out.println(optional.get());//Math::min返回1,Math::max返回9

        Integer i= IntStream.of()
                .reduce(Math::max) //终止操作：数组按顺序流进'数据流管道'，再两两比较，找出数组流中最小的值并返回,用Optional包装后返回 Optional<Integer>。返回Optional<Integer>比返回Integer更安全，区别是若Stream流是空的，则Math.min()会传入两个空值比较，进而报异常，这个异常被Optional捕捉并封装，同时Optional返回一个空值
                .orElse(0); //终止操作：若reduce没有返回值，则返回Integer型的0
        System.out.println(i);//返回0

        Stream<Integer> s=Stream.of(1,2,3,6,4,5).sorted();//对原流数组进行排序后返回:123456
        s.forEach(System.out::print);//排序返回:1,2,3,4,5,6

    }
}
