package com.uaepay.pub.csc.test.mocker;

import com.uaepay.mns.service.facade.MsgNotifyFacade;
import com.uaepay.mns.service.facade.domain.TemplateQueryRequest;
import com.uaepay.mns.service.facade.request.MsgNotifyRequest;
import com.uaepay.mns.service.facade.request.TemplateRegisterRequest;
import com.uaepay.mns.service.facade.response.MsgNotifyResult;
import com.uaepay.mns.service.facade.response.TemplateQueryResult;
import com.uaepay.mns.service.facade.response.TemplateRegisterResult;
import com.uaepay.pub.csc.test.checker.mns.MnsApplyNotifyMocker;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lzb
 */
@Service
public class MockMsgNotifyFacade implements MsgNotifyFacade {
    @Autowired
    MnsApplyNotifyMocker mnsApplyNotifyMocker;
    @Override
    public MsgNotifyResult applyNotify(MsgNotifyRequest request) {
        return mnsApplyNotifyMocker.getResult();
    }

    @Override
    public MsgNotifyResult queryResult(String msgOrderNo) {
        return null;
    }

    @Override
    public TemplateRegisterResult loginTemplate(TemplateRegisterRequest request) {
        return null;
    }

    @Override
    public TemplateQueryResult queryTemplate(TemplateQueryRequest request) {
        return null;
    }
}
