package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private WeatherController weatherController;

    private Logger logger = LoggerFactory.getLogger(WeatherController.class);


    @Test
    void contextLoads() {
        Thread[] th = new Thread[200];
        for (int i = 0; i < 200; i++) {
            th[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.info(weatherController.getTemperature("中国", "江苏", "苏州"));
                }
            });
            th[i].start();
        }
    }

}
