package com.redisdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class JedisPoolProperties {

   // @Value("${port}")
    private  int port;

   // @Value("${timeout}")
    private  int timeout;

    //@Value("${host}")
    private  String  host;

    //@Value("${jedis.pool.max-active")
    private int maxActive;

    //@Value("${jedis.pool.max-wait")
    private int maxWait;

   // @Value("${jedis.pool.min-idle")
    private int minIdle;

   // @Value("${jedis.pool.max-idle")
    private int maxIdle;


    @Bean
    public JedisPool redisPoolFactory() {
        //logger.info("JedisPool注入成功！！");
        //logger.info("redis地址：" + host + ":" + port);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        //jedisPoolConfig.setMaxTotal(maxActive);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

        return jedisPool;
    }

    /**
     * 获取jedispool
     *
     * @return
     */
  /*  @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig config = getJedisPoolConfig();
        System.out.println("strSingleNode:" + this.strSingleNode);
        String[] nodeArr = this.strSingleNode.split(":");

        JedisPool jedisPool = null;
        if (this.password.isEmpty()) {
            jedisPool = new JedisPool(
                    config,
                    nodeArr[0],
                    Integer.valueOf(nodeArr[1]),
                    this.timeout);
        } else {
            jedisPool = new JedisPool(
                    config,
                    nodeArr[0],
                    Integer.valueOf(nodeArr[1]),
                    this.timeout,
                    this.password);
        }
        return jedisPool;
    }*/

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }
}
