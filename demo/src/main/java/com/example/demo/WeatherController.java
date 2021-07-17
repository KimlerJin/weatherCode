package com.example.demo;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

/**
 * @author: Kimler Jin
 * Date: 2021/07/17 18:24
 * Content: DEMO
 */
@Controller
public class    WeatherController {


    private final RateLimiter rateLimiter = RateLimiter.create(100); //令牌桶 实现限流

    @Autowired
    private WeatherService weatherService;

    private Logger loggerFactory = LoggerFactory.getLogger(WeatherController.class);


    @ResponseBody
    @RequestMapping("/getTemperature")
    public String getTemperature(@RequestParam(name = "country") String country, @RequestParam(name = "province") String province, @RequestParam(name = "city") String city) {
        if (rateLimiter.tryAcquire()) {
            Optional<Integer> optionalInteger = Optional.empty();
            try {
                optionalInteger = weatherService.getTemperature(province, city, country);
            } catch (Exception e) {
                return e.getMessage();
            }
            return optionalInteger.isPresent() ? String.valueOf(optionalInteger.get()) : "获取不到指定信息！";
        } else {
            return "服务器正忙。。请稍后处理";
        }
    }


    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        Thread[] th = new Thread[200];
        for (int i = 0; i < 200; i++) {
            th[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    loggerFactory.info(getTemperature("中国", "江苏", "苏州"));
                }
            });
            th[i].start();
        }


        return "";
    }

}
