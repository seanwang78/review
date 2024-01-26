package com.uaepay.pub.csc.test.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
public class TestData {

    String group1;

    String group2;

    String group3;
    
    String dataSet;

    String dataType;

    String orderNo;

    String status;

    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal amount;

    String currency;


    Date updateTime;

}
