package com.chenlei.task;

import com.chenlei.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * Created by chenlei on 2017/8/28.
 */
@Component("DynamicTask2")
public class DynamicTask2 implements DynamicTask {
    public void run() {
        System.out.println(DateUtil.nowDateStr() + this.getClass().getName());
    }
}