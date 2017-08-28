package com.chenlei.task;

import org.springframework.stereotype.Component;

/**
 * Created by chenlei on 2017/8/28.
 */
@Component
public class TestTask implements DynamicTask {

    public void run(){
        System.out.println(this.getClass() + ".run()");
    }

}
