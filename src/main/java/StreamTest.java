import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.stream.Collectors;
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
        System.out.println(optional.get());//Math::min返回1,Math::max返回

        Integer i= IntStream.of()
                .reduce(Math::max) //终止操作：数组按顺序流进'数据流管道'，再两两比较，找出数组流中最小的值并返回,用Optional包装后返回 Optional<Integer>。返回Optional<Integer>比返回Integer更安全，区别是若Stream流是空的，则Math.min()会传入两个空值比较，进而报异常，这个异常被Optional捕捉并封装，同时Optional返回一个空值
                .orElse(0); //终止操作：若reduce没有返回值，则返回Integer型的0
        System.out.println(i);//返回0

        Stream<Integer> s=Stream.of(1,2,3,6,4,5).sorted();//对原流数组进行排序后返回:123456
        s.forEach(System.out::print);//排序返回:1,2,3,4,5,6

        System.out.println();
        Stream.of("afei","afei1")
                .flatMap(st->st.chars().mapToObj(stt->(char)stt))  //将多个字串合并成一个，并把合并后字串的每个字符先转成IntStream流，再合并成Charactor型的Stream流数组
                .collect(Collectors.toSet())  //转成Charactor型Set集合
                .stream()  //转成Charactor型流数组
                .collect(Collectors.toList()) //转成Charactor型List
                .forEach(System.out::print);  //遍历List

        //获取一个随机数组合，该组合有100W个数
        Random r=new Random();
        IntStream.range(0,1000000)  //步长1,生成1000000个数的int元素流数组
                .map(x->r.nextInt(1000000)) //遍历流数组每个元素，每次返回0-1000000间的一个数，最后返回一个int类型的流数组
                .boxed()  //对流数组里每个int元素装箱，返回Integer元素的流数组
                .collect(Collectors.toList());//将Integer元素的流数组转为List数组


        HashMap map=new HashMap();
        map.get("1");

    }
}
