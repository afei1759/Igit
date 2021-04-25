import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @Description 基于LinkedHashMap实现一个基于'LRU最近最少使用'算法的缓存，并且最多存MAX个值
 * @Author afei
 * @date:2021/4/25
 */
public class LRUCache<K,V> implements  Iterable{
    private LinkedHashMap<K,V> linkedHashMap=new LinkedHashMap<>();
    private final int MAX=3;

    public void cache(K key,V value){
        if(this.linkedHashMap.containsKey(key)){
            this.linkedHashMap.remove(key);
        }else if(this.linkedHashMap.size()==this.MAX){
            Iterator<K> it=this.linkedHashMap.keySet().iterator();
            this.linkedHashMap.remove(it.next());
        }
        this.linkedHashMap.put(key,value);  //linkedHashMap在jdk1.8之后用头插法
    }

    @Override
    public Iterator iterator() {  //iterator方法
        Iterator<Map.Entry<K, V>> it=this.linkedHashMap.entrySet().iterator();

        return new Iterator<V>(){

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public V next() {
                return it.next().getValue();
            }
        };
    }

    public  static void  main(String[] args){
        LRUCache<Integer,String> lru=new LRUCache<Integer,String>();
        lru.cache(1,"a");
        lru.cache(2,"b");
        lru.cache(3,"c");
        System.out.println(StreamSupport.stream(lru.spliterator(),false).map(x->x).collect(Collectors.joining("")));//abc
        lru.cache(2,"b");
        System.out.println(StreamSupport.stream(lru.spliterator(),false).map(x->x).collect(Collectors.joining("")));//acb

    }
}
