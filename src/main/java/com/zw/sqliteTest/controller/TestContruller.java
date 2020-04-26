package com.zw.sqliteTest.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import com.zw.sqliteTest.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 读写测试
 */
@Controller
@RequestMapping("/test")
public class TestContruller {
    @Autowired
    private TestService testService;

    /**
     * 获取当前主机进程信息
     */

    @PostMapping("/insertTest")
    @ResponseBody
    public String insertTest(@RequestBody JSONObject json) {
        return  testService.insertTest(Convert.toInt(json.get("num")));
    }


    /**
     * 获取当前主机进程信息
     */

    @PostMapping("/selectTest")
    @ResponseBody
    public String selectTest(@RequestBody JSONObject json) {
        return  testService.selectTest(Convert.toInt(json.get("num")));
    }


}
