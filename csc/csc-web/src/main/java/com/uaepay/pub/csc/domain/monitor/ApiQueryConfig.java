package com.uaepay.pub.csc.domain.monitor;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * API数据源查询配置
 * 
 * @author zc
 */
@Data
public class ApiQueryConfig {

    @NotBlank
    String appCode;

    String dataType;

    String queryParam;

}
