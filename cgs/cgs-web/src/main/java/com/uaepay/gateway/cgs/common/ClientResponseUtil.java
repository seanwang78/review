package com.uaepay.gateway.cgs.common;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/9
 * @since 0.1
 */
@Slf4j
public class ClientResponseUtil {

    /**
     * 处理客户端响应
     * @author Zhibin Liu
     * @time 2020/3/9 17:49
     * @param response
     */
    public static void handleResponseTID(ClientServiceResponse response) {
        String message = response.getHead().getMsg();

        // traceCode 如果不为空表示已处理过msg，直接返回
        if(StringUtils.isNotBlank(response.getHead().getTraceCode())) {
            return;
        }

        // 直接使用6位TID
        if(StringUtils.isNotBlank(message) && message.contains("#")) {
            String[] strs = message.split("#");
            response.getHead().setMsg(strs[0]);
        }

        setTraceCode(response);

/*        //判断acs是否有配置“#"
        if(StringUtils.isNotBlank(message) && message.contains("#")) {

            String[] strs = message.split("#");
            response.getHead().setMsg(strs[0]);

            if(strs.length > 1 ) {
                String bizCode = strs[1];
                if(StringUtils.isNotBlank(bizCode)) {
                    // 为了处理${validate.notBlank}:xxxx问题， 产品要求:xxx 不展示给用户看
                    bizCode = bizCode.substring(0,bizCode.length() >= 2 ? 2  : bizCode.length());
                } else {
                    bizCode = "";
                }
                log.info("处理#字符号前的原始msg：{},处理后文案:{}",message,strs[0]);

                // 设置traceCode
                setTraceCode(response, bizCode);
            }
        }else {
            setTraceCode(response,"");
        }*/
    }

/*    private static void setTraceCode(ClientServiceResponse response, String bizCode) {
        String code = response.getHead().getCode();
        // 下游返回成功不设置 traceCode
        if(!"200".equals(code)) {
            String tid = TraceContext.traceId();

            // 有bizCode 则加上 bizCode ，没有则取 4 位Tid，不然无法区分bizCode
            response.getHead().setTraceCode( bizCode + getTidBylength(tid,4,"0000"));
            log.info("original tid：{}, trade code {}", tid,response.getHead().getTraceCode());
        }
    }*/

    private static void setTraceCode(ClientServiceResponse response) {
        String code = response.getHead().getCode();
        // 下游返回成功不设置 traceCode
        if(!"200".equals(code)) {
            String tid = TraceContext.traceId();

            response.getHead().setTraceCode(getTidBylength(tid,6,"000000"));
            log.info("original tid：{}, trade code {}", tid,response.getHead().getTraceCode());
        }
    }

    public static String getTidBylength(String tid,int len,String defalut){

        if(StringUtils.isNoneBlank(tid)) {
            String interceptTid = tid.substring(tid.length() -len);
            return interceptTid;
        }
        return defalut;
    }

}
