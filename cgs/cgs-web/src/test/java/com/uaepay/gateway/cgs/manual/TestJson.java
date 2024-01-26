package com.uaepay.gateway.cgs.manual;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.gateway.cgs.domain.TmsDomain;

import java.util.Date;

/**
 * <p>描述: [类型描述] </p>
 * <p>创建时间: 17/01/2020 </p>
 *
 * @author caoyongxing
 * @version v1.0
 */
public class TestJson {

    public static void main(String[] args){
        TmsDomain tmsDomain=new TmsDomain();
        tmsDomain.setRequestTime(new Date());

        try {
            String s=JsonUtil.toJsonString(tmsDomain);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
