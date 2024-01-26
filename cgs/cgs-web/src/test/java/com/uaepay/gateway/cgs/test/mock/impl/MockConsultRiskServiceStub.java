package com.uaepay.gateway.cgs.test.mock.impl;

import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.rm.cps.stub.cpsstub.ConsultRiskServiceStub;
import com.uaepay.rm.cps.stub.cpsstub.misc.Decission;
import com.uaepay.rm.cps.stub.cpsstub.misc.ErrorMsg;
import com.uaepay.rm.cps.stub.cpsstub.vo.*;
import com.uaepay.rm.unbreakable.Option;
import com.uaepay.rm.unbreakable.Result;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class MockConsultRiskServiceStub implements ConsultRiskServiceStub {

    @Autowired
    AccessMocker accessMocker;

    @Override
    public Result<ErrorMsg, Decission> memberProductPermissionCheck(List<MemberProductPermissionCheck> params,
        long timestamp) {
        for (MemberProductPermissionCheck param : params) {
            if (param.getCheckType() == MemberProductPermissionCheck.CheckEnum.MemberID) {
                MockAccess access = accessMocker.getByMemberId(param.getIds().get(0));
                if (access == null) {
                    continue;
                } else if (access.isPassMemberPermissionCheck()) {
                    return Result.liftRight(Decission.passed(null));
                } else {
                    return Result.liftLeft(ErrorMsg.EmptyError("mock fail"));
                }
            }
        }
        return Result.liftLeft(ErrorMsg.EmptyError("unmock fail"));
    }

    @Override
    public Result<ErrorMsg, Decission> cgsGatewayLegalityCheck(CgsGatewayLegalityCheckParam param) {
        return Result.liftRight(Decission.passed(null));
    }

    @Override
    public Result<ErrorMsg, Decission> consultRiskManager(ConsultRequest consultRequest) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Decission> memberFilter(ConsultRequest consultRequest) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Decission> memberFeatureFilter(List<MemberFeatureFilter> filters, FilterRelation relation, long timestamp, HostApp hostApp) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Decission> memberProductPermissionCheckV2(List<MemberProductPermissionCheck> params, long timestamp) {
        return null;
    }

    @Override
    public Result<ErrorMsg, ListDataResult> selectAllBlackWhiteData() {
        return null;
    }

    @Override
    public Result<ErrorMsg, Boolean> saveOneRepoData(ListDataParam listDataParam) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Boolean> updateOneRepoDataById(ListDataParam listDataParam) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Boolean> deleteOneRepoDataByValue(ListDataParam listDataParam) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Boolean> deleteOneRepoDataById(ListDataParam listDataParam) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Option<Page>> selectRepoData(ListDataParam dataParam, Page page) {
        return null;
    }

    @Override
    public Result<ErrorMsg, Boolean> deleteRepoDataByValue(List<ListDataParam> paramList) {
        return null;
    }

}
