package com.redisdemo.controller;

import com.redisdemo.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Auther: cdc
 * @Date: 2020/2/27 15:14
 * @Description: 模拟抢单，并发测试redis分布式锁
 * 链接：https://mp.weixin.qq.com/s/sat9UJGvmtWvo1kfB-zD3A 或者印象笔记
 */

@Controller
@RequestMapping("/order")
public class QiangDanConrtoller {

    private static final Logger logger = LoggerFactory.getLogger(QiangDanConrtoller.class);

    @Autowired
    private JedisUtil jedisUtil;

    //总库存
    private long nKuCuen = 0;
    //商品key名字
    private String shangpingKey = "computer_key";
    //获取锁的超时时间 秒
    private int timeout = 30 * 1000;

    // 如下初始化10w个用户，并初始化库存，商品等信息
    @GetMapping("/qiangdan")
    @ResponseBody
    public List<String> qiangdan() {

        //抢到商品的用户
        List<String> shopUsers  = new ArrayList<>();
        // 构造很多用户
        List<String> users = new ArrayList<>(100000);
        IntStream.range(0,100000).parallel().forEach(b->{
            users.add("神牛-"+b);
        });

        // 初始化库存
        nKuCuen = 10;

        // 模拟开枪
        users.parallelStream().forEach(b->{
            String shopUser = qiang(b);
            if (!StringUtils.isEmpty(shopUser)) {
                shopUsers.add(shopUser);
            }
        });

        return shopUsers;
    }

    // 有了上面10w个不同用户，我们设定商品只有10个库存，然后通过并行流的方式来模拟抢购，如下抢购的实现：

    /**
     * 模拟抢单动作
     */
    private String qiang(String b) {
        //用户开抢时间
        long startTime = System.currentTimeMillis();

        //未抢到的情况下，30秒内继续获取锁
        while ((startTime + timeout) >= System.currentTimeMillis()) {
            //商品是否剩余
            if (nKuCuen <= 0) {
                break;
            }
            if (jedisUtil.set(shangpingKey, b,timeout)) {
                //用户b拿到锁
                logger.info("用户{}拿到锁...", b);
                try {
                    //商品是否剩余
                    if (nKuCuen <= 0) {
                        break;
                    }

                    //模拟生成订单耗时操作，方便查看：神牛-50 多次获取锁记录
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //抢购成功，商品递减，记录用户
                    nKuCuen -= 1;

                    //抢单成功跳出
                    logger.info("用户{}抢单成功跳出...所剩库存：{}", b, nKuCuen);

                    return b + "抢单成功，所剩库存：" + nKuCuen;
                } finally {
                    logger.info("用户{}释放锁...", b);
                    //释放锁
                    jedisUtil.delnx(shangpingKey, b);
                }
            } else {
                //用户b没拿到锁，在超时范围内继续请求锁，不需要处理
//                if (b.equals("神牛-50") || b.equals("神牛-69")) {
//                    logger.info("用户{}等待获取锁...", b);
//                }
            }
        }
        return "";
    }
}
