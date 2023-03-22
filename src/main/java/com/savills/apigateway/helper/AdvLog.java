package com.savills.apigateway.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;


@Data
public class AdvLog extends BaseLog{

    private String msg;
    public AdvLog()
    {
        super(null);
    }

    @Override
    public String toString() {
        // In Java 8 and later, StringJoiner is used to construct a sequence of characters separated by a delimiter and optionally starting with a supplied prefix and ending with a supplied suffix.
        return new StringJoiner(
                " | " ,
                AdvLog.class.getSimpleName() + "[ " ,
                " ]"
        )
                .add( "msg=" + msg )

                .toString();
    }
}
