package com.lazai.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yozora
 * <p>
 * 每个操作都有自动重试策略(retryAttempts、retryInterval)
 */
@Log4j2
@Component
public final class RedisUtils {

    @Autowired
    private RedissonClient getRedissonClient;

    private static RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        redissonClient = getRedissonClient;
    }


    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * set cache expire time
     *
     * @param key  key
     * @param time expire time in seconds
     * @return boolean
     * @author yozora
     */
    public static boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redissonClient.getKeys().expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get cache expire time
     *
     * @param key key
     * @return expire time in seconds, -1 means never expire, -2 means key not exist
     */
    public static long getExpire(String key) {
        long remainTimeToLive = redissonClient.getKeys().remainTimeToLive(key);
        if (remainTimeToLive == -1) {
            return -1;
        } else if (remainTimeToLive == -2) {
            return -2;
        } else {
            return remainTimeToLive / 1000L;
        }
    }

    /**
     * check if key exists
     *
     * @param keys keys
     * @return number of keys that exist
     */
    public static long hasKey(String... keys) {
        try {
            return redissonClient.getKeys().countExists(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * delete cache
     *
     * @param keys keys
     */
    public static boolean delKys(String... keys) {
        if (keys != null && keys.length > 0) {
            long delete = redissonClient.getKeys().delete(keys);
            return delete != 0L;
        }
        return false;
    }

    /**
     * scan keys
     *
     * @param pattern key pattern
     * @param limit   limit
     * @return java.lang.Iterable<java.lang.String>
     * @author yozora
     **/
    public static Iterable<String> scanKeys(String pattern, int limit) {
        try {
            return redissonClient.getKeys().getKeysByPattern(pattern, limit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // ============================String=============================

    public static boolean initBucketCacheKeyListener(String key) {
        int addListener = redissonClient.getBucket(key).addListener((ExpiredObjectListener) s -> {
            System.out.println("ExpiredObjectListener: " + s);
        });
        return addListener > 0;
    }

    /**
     * get value from cache
     *
     * @param key key
     * @return value
     */
    public static String get(String key) {
        if (redissonClient.getBucket(key).isExists()) {
            return (String) redissonClient.getBucket(key).get();
        } else {
            return null;
        }
    }

    /**
     * set(px,nx)
     * @param key
     * @param value
     * @param duration
     * @return
     */
    public static boolean setIfAbsent(String key, Object value, Duration duration){
        try {
            return redissonClient.getBucket(key).setIfAbsent(value, duration);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * set cache
     *
     * @param key   key
     * @param value value
     * @return boolean
     */
    public static boolean set(String key, Object value) {
        try {
            redissonClient.getBucket(key).set(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * set cache with expire time
     *
     * @param key   key
     * @param value value
     * @param time  expire time in seconds, if time <= 0, it will never expire
     * @return boolean
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redissonClient.getBucket(key).set(value, Duration.ofSeconds(time));
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ================================Hash=================================

    /**
     * init cache key listener
     *
     * @param key hash key
     * @return boolean
     * @author yozora
     */
    public static boolean initCacheKeyListener(String key) {
        RMap<Object, Object> redissonClientMap = redissonClient.getMap(key);
        int addListener = redissonClientMap.addListener((DeletedObjectListener) s -> {
            log.info("map object: {} removed", s);
        });
        return addListener > 0;
    }

    /**
     * HashGet
     *
     * @param key  key
     * @param item item
     * @return value
     */
    public static Object hget(String key, String item) {
        return redissonClient.getMap(key).get(item);
    }


    /**
     * get all keys in hash
     *
     * @param key key
     * @return keys
     */
    public static Collection<Object> hmgetKey(String key) {
        return redissonClient.getMap(key).readAllKeySet();
    }

    /**
     * get all values in hash
     *
     * @param key key
     * @return values
     */

    public static Map<Object, Object> hmget(String key) {
        return redissonClient.getMap(key).readAllMap();
    }


    /**
     * HashSet
     *
     * @param key key
     * @param map map
     * @return boolean
     */

    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            redissonClient.getMap(key).putAll(map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * HashSet with expire time
     *
     * @param key  key
     * @param map  map
     * @param time time to live in seconds
     * @return boolean
     */
    public static boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redissonClient.getMap(key).putAll(map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * put key-value to hash, if key exists, cover it
     *
     * @param key   key
     * @param item  item
     * @param value value
     * @return boolean
     */
    public static boolean hsetMayCover(String key, String item, Object value) {
        try {
            redissonClient.getMap(key).put(item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * put key-value to hash with expire time, if key exists, cover it
     *
     * @param key   key
     * @param item  item
     * @param value value
     * @param time  time to live in seconds
     * @return boolean
     */
    public static boolean hsetMayCover(String key, String item, Object value, long time) {
        try {
            redissonClient.getMap(key).put(item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * put key-value to hash, if key exists, do nothing
     *
     * @param key   key
     * @param item  item
     * @param value value
     * @return boolean
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            return redissonClient.getMap(key).fastPutIfAbsent(item, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * put key-value to hash with expire time, if key exists, do nothing
     *
     * @param key   key
     * @param item  item
     * @param value value
     * @param time  time to live in seconds
     * @return boolean
     */
    public static boolean hset(String key, String item, Object value, long time) {
        try {
            redissonClient.getMap(key).fastPutIfAbsent(item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * delete values in hash
     *
     * @param key   key
     * @param items items
     * @return number of items deleted
     */


    public static long hdel(String key, Object... items) {
        return redissonClient.getMap(key).fastRemove(items);
    }

    /**
     * check if hash has key
     *
     * @param key  key
     * @param item item
     * @return boolean
     */

    public static boolean hHasKey(String key, String item) {
        return redissonClient.getMap(key).containsKey(item);
    }

    // ============================set=============================

    /**
     * get set cache
     *
     * @param key key
     * @return Set<Object>
     */
    public static Set<Object> sGet(String key) {
        try {
            return redissonClient.getSet(key).readAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * check if set has value
     *
     * @param key   key
     * @param value value
     * @return boolean
     */
    public static boolean sHasKey(String key, Object value) {
        try {
            return redissonClient.getSet(key).contains(value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * put set data to cache
     *
     * @param key    key
     * @param values values
     * @return number of values added
     */
    public static boolean sSet(String key, Object... values) {
        try {
            return redissonClient.getSet(key).addAll(Arrays.asList(values));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */


    public static boolean sSetAndTime(String key, long time, Object... values) {
        try {
            boolean addAll = redissonClient.getSet(key).addAll(Arrays.asList(values));
            if (time > 0) {
                expire(key, time);
            }
            return addAll;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long sGetSetSize(String key) {
        try {
            return redissonClient.getSet(key).size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static boolean setRemove(String key, Object... values) {
        try {
            return redissonClient.getSet(key).remove(values);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================sorted set=============================

    /**
     * 功能描述: 获取序列数据
     *
     * @param key       键
     * @param sortOrder 排序方式
     * @param offset    起始位置
     * @param count     数量
     * @return java.util.Collection<java.lang.Object>
     * @author yozora
     * @date 2021/8/12 14:07
     */
    public static Collection<Object> sortedGet(String key, SortOrder sortOrder, int offset, int count) {
        try {
            return redissonClient.getScoredSortedSet(key).readSort(sortOrder, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据key获取sorted set中的所有值
     *
     * @param key 键
     * @return Collection<Object>
     */
    public static Collection<Object> sortedGetAll(String key) {
        try {
            return redissonClient.getScoredSortedSet(key).readAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个sorted set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public static boolean sortedHasKey(String key, Object value) {
        try {
            return redissonClient.getScoredSortedSet(key).contains(value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 功能描述: 序列新增对象
     *
     * @param key   键
     * @param score 积分
     * @param value 对象值
     * @return boolean
     * @author yozora
     * @date 2021/8/12 13:56
     */
    public static boolean sortedSetObject(String key, double score, Object value) {
        try {
            return redissonClient.getScoredSortedSet(key).add(score, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * description: 序列修改时间
     *
     * @param key 键
     * @return boolean success
     * @author yozora
     * @date 17:34 2022/9/15
     **/
    public static boolean sortedSetTime(String key) {
        return redissonClient.getScoredSortedSet(key).expire(Instant.now().plus(7, ChronoUnit.DAYS));
    }

    /**
     * 功能描述: 序列新增对象并设置失效时间
     *
     * @param key   键
     * @param score 积分
     * @param value 对象值
     * @return boolean
     * @author yozora
     * @date 2021/8/12 13:56
     */
    public static boolean sortedSetObjectAndTime(String key, long time, double score, Object value) {
        try {
            boolean add = redissonClient.getScoredSortedSet(key).add(score, value);
            if (time > 0) {
                expire(key, time);
            }
            return add;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 功能描述: 序列修改积分
     *
     * @param key   键
     * @param score 积分
     * @param value 对象
     * @return int 当前积分
     * @author yozora
     * @date 2021/8/12 14:00
     */
    public static int sortedSetScore(String key, double score, Object value) {
        try {
            return redissonClient.getScoredSortedSet(key).addScoreAndGetRank(value, score);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 功能描述: 获取序列分数
     *
     * @param key   键名
     * @param value 值
     * @return java.lang.Double
     * @author yozora
     * @date 2021/10/18 11:04
     */
    public static Double sortedGetScore(String key, Object value) {
        try {
            return redissonClient.getScoredSortedSet(key).getScore(value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0D;
        }
    }

    /**
     * 功能描述: 获取序列大小
     *
     * @param key 键
     * @return long
     * @author yozora
     * @date 2021/8/12 14:01
     */
    public static long sortedGetSetSize(String key) {
        try {
            return redissonClient.getScoredSortedSet(key).size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 功能描述: 序列移除对象
     *
     * @param key   键
     * @param value 对象
     * @return boolean
     * @author yozora
     * @date 2021/8/12 14:02
     */
    public static boolean sortedRemove(String key, Object value) {
        try {
            return redissonClient.getScoredSortedSet(key).remove(value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================list=================================


    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static List<Object> lGet(String key, int start, int end) {
        try {
            return redissonClient.getList(key).range(start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public static long lGetListSize(String key) {
        try {
            return redissonClient.getList(key).size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * description: 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return java.lang.Object
     * @author yozora
     * @date 18:08 2022/5/20
     **/
    public static Object lGetIndex(String key, int index) {
        try {
            return redissonClient.getList(key).get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * description: 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return boolean
     * @author yozora
     * @date 18:08 2022/5/20
     **/
    public static boolean lSet(String key, Object value) {

        try {
            redissonClient.getList(key).add(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * description:  将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return boolean
     * @author yozora
     * @date 18:09 2022/5/20
     **/
    public static boolean lSet(String key, Object value, long time) {
        try {
            redissonClient.getList(key).add(value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static boolean lSet(String key, List<Object> value) {
        try {
            redissonClient.getList(key).addAll(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            redissonClient.getList(key).addAll(value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static boolean lUpdateIndex(String key, int index, Object value) {
        try {
            redissonClient.getList(key).set(index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static boolean lRemove(String key, int count, Object value) {
        try {
            return redissonClient.getList(key).remove(value, count);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //============================Queue=======================

    /**
     * 非阻塞队列中添加数据
     *
     * @param key   队列键值
     * @param value 队列对象
     * @return boolean
     */
    public static boolean queueAdd(String key, Object value) {
        RQueue<Object> queue = redissonClient.getQueue(key);
        return queue.add(value);
    }

    /**
     * description: get queue data
     *
     * @param key key
     * @return java.lang.Object
     * @author yozora
     **/
    public static Object queuePeek(String key) {
        RQueue<Object> queue = redissonClient.getQueue(key);
        return queue.peek();
    }

    /**
     * 非阻塞队列中获取并删除数据
     *
     * @param key 队列键值
     * @return 队列对象
     */
    public static Object queuePoll(String key) {
        RQueue<Object> queue = redissonClient.getQueue(key);
        return queue.poll();
    }

    /**
     * 功能描述: 获取redis锁
     *
     * @param lockKey 锁字段
     * @return boolean
     * @author yozora
     */
    public static RLock getLock(String lockKey) throws InterruptedException {
        return redissonClient.getLock(lockKey);
    }


}
