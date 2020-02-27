package com.redisdemo.controller;

import com.google.gson.Gson;
import com.redisdemo.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/redis_demo")
public class RedisDemoController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JedisUtil jedisUtil;

    @RequestMapping("/set/redis_key")
    @ResponseBody
    private boolean setRedisKey() {
       // stringRedisTemplate.opsForValue().setIfAbsent("username","cdc");
        //stringRedisTemplate.delete("username");
        boolean isSuccess = jedisUtil.set("cdc", "aaa", 10 * 1000);
        return isSuccess;
    }

    @GetMapping("/get/{key}")
    @ResponseBody
    public String getKey(@PathVariable String key) {
        Gson gson = new Gson();
        String value = jedisUtil.get(key);
        System.out.println("====key: "+key + " " + "value: " + value);
        return value;
    }

    @GetMapping("/delnx/{key}/{val}")
    @ResponseBody
    public int delnx(@PathVariable String key, @PathVariable String val) {
        return jedisUtil.delnx(key, val);
    }
}
