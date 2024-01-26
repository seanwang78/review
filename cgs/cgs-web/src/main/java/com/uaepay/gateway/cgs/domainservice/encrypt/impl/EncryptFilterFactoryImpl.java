package com.uaepay.gateway.cgs.domainservice.encrypt.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilter;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilterFactory;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategyFactory;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.gateway.cgs.domainservice.encrypt.strategy.DefaultEncryptStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EncryptFilterFactoryImpl implements EncryptFilterFactory {

    private static final String ITEM_DELIMITER = ",";
    private static final String PATH_DELIMITER = "\\.";
    private static final String STRATEGY_DELIMITER = ":";

    private EncryptStrategyFactory encryptStrategyFactory;

    private final ConcurrentHashMap<String, EncryptFilter> filterMap = new ConcurrentHashMap<>();

    public EncryptFilterFactoryImpl(EncryptStrategyFactory encryptStrategyFactory) {
        this.encryptStrategyFactory = encryptStrategyFactory;
    }

    @Override
    public EncryptFilter create(String encryptConfig) {
        if (StringUtils.isBlank(encryptConfig)) {
            return null;
        }
        EncryptFilter filter = filterMap.get(encryptConfig);
        if (filter != null) {
            return filter;
        }
        synchronized (filterMap) {
            filter = filterMap.get(encryptConfig);
            if (filter != null) {
                return filter;
            }
            filter = parse(encryptConfig);
            filterMap.put(encryptConfig, filter);
        }
        return filter;
    }

    private EncryptFilter parse(String encryptConfig) {
        List<EncryptFilter> filters = new ArrayList<>();
        String[] filterConfigs = encryptConfig.split(ITEM_DELIMITER);
        for (String filterConfig : filterConfigs) {
            if (StringUtils.isBlank(filterConfig)) {
                continue;
            }
            String[] steps = filterConfig.trim().split(PATH_DELIMITER);
            if (steps.length >= 1) {
                String lastStep = steps[steps.length - 1];
                String strategyCode = DefaultEncryptStrategy.STRATEGY_CODE;
                if (lastStep.contains(":")) {
                    String[] lastSteps = lastStep.split(STRATEGY_DELIMITER);
                    steps[steps.length - 1] = lastSteps[0];
                    strategyCode = lastSteps[1];
                }
                EncryptStrategy strategy = encryptStrategyFactory.getService(strategyCode);
                if (strategy == null) {
                    throw new ErrorException("加密策略配置有误: " + strategyCode);
                }
                filters.add(new Filter(Arrays.asList(steps), strategy));
            }
        }
        return new ComposedEncryptFilter(filters);
    }

    public class Filter implements EncryptFilter {

        public Filter(List<String> steps, EncryptStrategy encryptStrategy) {
            this.steps = steps;
            this.encryptStrategy = encryptStrategy;
        }

        List<String> steps;

        EncryptStrategy encryptStrategy;

        @Override
        public void filterReplace(JSONObject body, ContextParameter parameter) {
            filter(body, 0, parameter);
        }

        private void filter(Object current, int stepIndex, ContextParameter parameter) {
            String stepKey = steps.get(stepIndex);
            if (isEnd(stepIndex)) {
                if (current instanceof JSONObject) {
                    replace((JSONObject)current, stepKey, parameter);
                } else if (current instanceof JSONArray) {
                    for (Object item : (JSONArray)current) {
                        if (item instanceof JSONObject) {
                            replace((JSONObject)item, stepKey, parameter);
                        }
                    }
                }
            } else {
                if (current instanceof JSONObject) {
                    filter(((JSONObject)current).get(stepKey), stepIndex + 1, parameter);
                } else if (current instanceof JSONArray) {
                    for (Object item : (JSONArray)current) {
                        filter(item, stepIndex + 1, parameter);
                    }
                }
            }
        }

        private boolean isEnd(int stepIndex) {
            return stepIndex == steps.size() - 1;
        }

        private void replace(JSONObject cur, String stepKey, ContextParameter parameter) {
            if (cur == null) {
                return;
            }
            Object orig = cur.get(stepKey);
            if (orig instanceof String) {
                cur.put(stepKey, encryptStrategy.convert(stepKey, (String)orig, parameter));
            }
        }

    }

}
