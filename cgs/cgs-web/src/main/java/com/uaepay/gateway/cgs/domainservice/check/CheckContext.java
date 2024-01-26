package com.uaepay.gateway.cgs.domainservice.check;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查上下文 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-27 13:19.
 */
@Data
@NoArgsConstructor
public class CheckContext {

    // 请求body
    private JSONObject jsonObject;

    private String requestCheckConfig;

    private String memberId;


    public CheckContext(JSONObject jsonObject,String requestCheckConfig){
        this.jsonObject = jsonObject;
        this.requestCheckConfig = requestCheckConfig;
    }

}
