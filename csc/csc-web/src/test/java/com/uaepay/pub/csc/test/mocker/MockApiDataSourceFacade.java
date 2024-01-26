package com.uaepay.pub.csc.test.mocker;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.datasource.facade.ApiDataSourceFacade;
import com.uaepay.pub.csc.datasource.facade.request.QueryDataRequest;
import com.uaepay.pub.csc.datasource.facade.response.QueryDataResponse;

@Service(version = "${spring.application.name}")
public class MockApiDataSourceFacade implements ApiDataSourceFacade {

    @Autowired
    QueryDataMocker queryDataMocker;

    @Override
    public QueryDataResponse queryData(QueryDataRequest request) {
        return queryDataMocker.getResponse(request);
    }

}
