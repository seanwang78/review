package com.uaepay.gateway.cgs.domainservice.check;

import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 校验处理器 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-19 18:07.
 */
@Service
public class CheckHandle {

    private static final String ITEM_DELIMITER = ",";
    private static final String STRATEGY_DELIMITER = ":";

    @Autowired
    private CheckStrategyFactory checkStrategyFactory;

    public void check(CheckContext context) {
        if (StringUtils.isBlank(context.getRequestCheckConfig())) {
            return;
        }
        String[] filterConfigs = context.getRequestCheckConfig().split(ITEM_DELIMITER);
        for (String filterConfig : filterConfigs) {
            if (StringUtils.isBlank(filterConfig)) {
                continue;
            }
            String[] splits = filterConfig.split(STRATEGY_DELIMITER);

            if (StringUtils.isEmpty(splits[1])) {
                throw new GatewayFailException(CgsReturnCode.REQUEST_CHECK_CONFIG_ERROR);
            }

            CheckStrategy strategy = checkStrategyFactory.getService(splits[1]);

            if (strategy == null) {
                throw new GatewayFailException(CgsReturnCode.REQUEST_CHECK_CONFIG_ERROR);
            }

            strategy.check(context, splits[0]);
        }
    }
}
