package com.uaepay.gateway.cgs.integration.cps;

import java.util.List;

import com.uaepay.rm.cps.stub.cpsstub.vo.CgsGatewayLegalityCheckParam;
import com.uaepay.rm.cps.stub.cpsstub.vo.MemberFeatureFilter;

/**
 * @author zc
 */
public interface CpsService {

    /**
     * @return 是否验证通过
     */
    boolean memberProductPermissionCheck(List<MemberFeatureFilter>  params,String hostAPP);

    void cgsGatewayLegalityCheck(CgsGatewayLegalityCheckParam param);

}
