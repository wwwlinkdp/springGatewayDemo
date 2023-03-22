package com.savills.apigateway.config;

import com.savills.apigateway.generator.mapper.InvoiceDetail0Mapper;
import com.savills.apigateway.helper.LoggerThread;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
@Component public class ApplicationStartup implements ApplicationListener < ApplicationReadyEvent > {
    @Override public void onApplicationEvent(final ApplicationReadyEvent event) {
        System.out.println("Application started!");
        InvoiceDetail0Mapper bean =
        event.getApplicationContext().getBean(InvoiceDetail0Mapper.class);
        LoggerThread.getInstance().setMapper(bean);
        return;
    }
}
