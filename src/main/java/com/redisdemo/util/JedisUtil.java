package com.redisdemo.util;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

@Component
public class JedisUtil {

    @Autowired
    private JedisPool jedisPool;

    private Gson gson = new Gson();


    /**
     * @param key
     * @param val
     * @param time 单位：秒
     * @return
     */
    public boolean set(String key, String val, long time) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (time <= 0) {
                return "OK".equalsIgnoreCase(jedis.set(key, val));
            } else {
                //NX:不存在key创建
                //EX:秒
                //PX: 毫秒
                return "OK".equalsIgnoreCase(jedis.set(key, val, new SetParams().px(time).nx()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public <T> boolean set(String key,T val,long time) {
        return set(key, gson.toJson(val), time);
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return "";
    }

    public <T> T get(String key, Class<T> tClass) {
        String val = this.get(key);
        if (val == null || val.isEmpty()) {
            return null;
        }
        return gson.fromJson(val, tClass);
    }

    /**
     * 解锁
     *
     * 这里也使用了jedis方式，直接执行lua脚本：根据val判断其是否存在，如果存在就del；
     * 其实个人认为通过jedis的get方式获取val后，然后再比较value是否是当前持有锁的用户，
     * 如果是那最后再删除，效果其实相当；只不过直接通过eval执行脚本，
     * 这样避免多一次操作了redis而已，缩短了原子操作的间隔。(如有不同见解请留言探讨)；
     *
     * @param key
     * @param val
     * @return
     */
    public int delnx(String key, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis == null) {
                return 0;
            }

            //if redis.call('get','orderkey')=='1111' then return redis.call('del','orderkey') else return 0 end
            StringBuilder sbScript = new StringBuilder();
            sbScript.append("if redis.call('get','").append(key).append("')").append("=='").append(val).append("'").
                    append(" then ").
                    append("    return redis.call('del','").append(key).append("')").
                    append(" else ").
                    append("    return 0").
                    append(" end");

            return Integer.valueOf(jedis.eval(sbScript.toString()).toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

}
