package com.uaepay.gateway.cgs.domainservice.check.strategy;

import com.uaepay.gateway.cgs.domainservice.check.CheckContext;
import com.uaepay.gateway.cgs.domainservice.check.CheckStrategy;
import com.uaepay.gateway.cgs.integration.ufs.UfsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Ufs 校验 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-19 17:44.
 */
@Service
public class UfsCheckStrategy implements CheckStrategy {

    public static final String STRATEGY_CODE = "UFS";

    @Autowired
    private UfsImageService ufsImageService;

    @Override
    public void check(CheckContext context, String fieldName) {
        Object value = context.getJsonObject().get(fieldName);
        if(value instanceof  String) {
            ufsImageService.checkFileId(Arrays.asList((String) value),context.getMemberId());
        }
    }

    @Override
    public String getServiceCode() {
        return STRATEGY_CODE;
    }
}
