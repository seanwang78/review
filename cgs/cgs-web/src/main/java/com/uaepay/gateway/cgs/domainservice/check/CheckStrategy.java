package com.uaepay.gateway.cgs.domainservice.check;

import com.uaepay.basis.beacon.common.template.CodeService;

/**
 * 校验策略 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-19 17:41.
 */
public interface CheckStrategy extends CodeService {

    void check(CheckContext context,String fieldName);
}
