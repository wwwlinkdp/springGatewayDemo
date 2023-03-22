package com.savills.apigateway.helper;

import lombok.Data;

@Data
public class BaseLog {
    private String spname;
    public BaseLog(String name)
    {
        this.spname = name;
    }
}
