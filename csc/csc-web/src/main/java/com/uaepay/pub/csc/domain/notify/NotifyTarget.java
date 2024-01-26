package com.uaepay.pub.csc.domain.notify;

import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通知目标
 * 
 * @author zc
 */
@Data
@AllArgsConstructor
public class NotifyTarget {

    NotifyTypeEnum notifyType;

    String targetIdentity;

}
