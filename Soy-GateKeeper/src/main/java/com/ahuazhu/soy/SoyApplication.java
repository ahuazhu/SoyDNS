package com.ahuazhu.soy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zhengwenzhu on 2017/3/28.
 */

@SpringBootApplication
public class SoyApplication {

    private volatile static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(SoyApplication.class, args);

        while (! stop) {
            Thread.sleep(1000 * 10);
        }
    }

}
