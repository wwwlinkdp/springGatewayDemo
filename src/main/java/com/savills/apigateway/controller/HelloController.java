package com.savills.apigateway.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.savills.apigateway.generator.domain.InvoiceDetail0;
import com.savills.apigateway.generator.mapper.InvoiceDetail0Mapper;

import com.savills.apigateway.helper.AdvLog;
import com.savills.apigateway.helper.LoggerThread;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class HelloController {

    @Resource
    private InvoiceDetail0Mapper invoiceDetail0Mapper;
    @GetMapping("/")
    public List<InvoiceDetail0> index() {

       // DateUtils
        //invoiceDetail0Mapper.insert()
        List<InvoiceDetail0> list = invoiceDetail0Mapper.selectList(null);

        for (int i = 0; i < 10; i++) {
            AdvLog item2 = new AdvLog();
            item2.setMsg(i+"");
            LoggerThread.getInstance().log(item2);
        }

        return list;
    }

}
