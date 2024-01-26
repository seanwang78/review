package com.uaepay.gateway.cgs.integration.cps.impl;

import com.uaepay.gateway.cgs.integration.cps.CpsService;
import com.uaepay.rm.cps.stub.cpsstub.ConsultRiskServiceStub;
import com.uaepay.rm.cps.stub.cpsstub.misc.Decission;
import com.uaepay.rm.cps.stub.cpsstub.misc.ErrorMsg;
import com.uaepay.rm.cps.stub.cpsstub.vo.CgsGatewayLegalityCheckParam;
import com.uaepay.rm.cps.stub.cpsstub.vo.HostApp;
import com.uaepay.rm.cps.stub.cpsstub.vo.MemberFeatureFilter;
import com.uaepay.rm.unbreakable.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zc
 */
@Service
public class CpsServiceImpl implements CpsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private ConsultRiskServiceStub consultRiskServiceStub;

    @Override
    public boolean memberProductPermissionCheck(List<MemberFeatureFilter>  params,String hostAPP) {
        Result<ErrorMsg, Decission> result =consultRiskServiceStub.memberFeatureFilter(params,
				MemberFeatureFilter.orFilterRelationInstance().getFilterRelation(), System.currentTimeMillis(),
                HostApp.getBySdk(hostAPP));
        if (result.isRight()) {
            return Decission.isPassed(result.rightValue().unsafeGet());
        } else {
            return false;
        }
    }

    @Override
    public void cgsGatewayLegalityCheck(CgsGatewayLegalityCheckParam param) {
        Result<ErrorMsg, Decission> result = consultRiskServiceStub.cgsGatewayLegalityCheck(param);
        String message = result(result);
    }

    private String result(Result<ErrorMsg, Decission> result) {
        if (result.isLeft()) {
            return "error,code:" + result.leftValue().unsafeGet().code() + ",msg:"
                + result.leftValue().unsafeGet().message();
        } else {
            if (Decission.isPassed(result.rightValue().unsafeGet())) {
                return "PASS";
            } else if (Decission.isRejected(result.rightValue().unsafeGet())) {
                return result.rightValue().unsafeGet().asRejected().rightValue().unsafeGet().message;
            } else if (Decission.isPending(result.rightValue().unsafeGet())) {
                return result.rightValue().unsafeGet().asPending().rightValue().unsafeGet().message;
            }
        }
        return "";
    }

}
