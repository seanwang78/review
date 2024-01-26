package com.uaepay.pub.csc.test.mocker;

import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.compensation.facade.CompensationFacade;
import com.uaepay.pub.csc.compensation.facade.request.CompensateSingleRequest;

@Service(version = "${spring.application.name}")
public class MockCompensationFacade implements CompensationFacade {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public CommonResponse applySingle(CompensateSingleRequest request) {
        logger.info("applySingle: {}", request);
        return CommonResponse.buildSuccess();
    }

}
