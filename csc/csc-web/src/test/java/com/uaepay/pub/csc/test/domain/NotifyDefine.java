package com.uaepay.pub.csc.test.domain;

import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class NotifyDefine {

    DefineTypeEnum defineType;

    long defineId;

}
