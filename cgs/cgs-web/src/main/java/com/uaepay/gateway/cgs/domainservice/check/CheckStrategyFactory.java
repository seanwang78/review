package com.uaepay.gateway.cgs.domainservice.check;

import com.uaepay.basis.beacon.common.template.AbstractCodeServiceFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检查策略工厂 .
 * <p>
 *
 * @author yusai
 * @date 2020-02-19 17:47.
 */
@Service
public class CheckStrategyFactory extends AbstractCodeServiceFactory<CheckStrategy> {

    public CheckStrategyFactory(List<CheckStrategy> checkStrategy) {
        super(checkStrategy);
    }

    @Override
    protected String getFactoryName() {
        return "检查策略工厂";
    }
}
