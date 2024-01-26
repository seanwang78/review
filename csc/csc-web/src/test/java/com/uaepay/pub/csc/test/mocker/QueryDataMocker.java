package com.uaepay.pub.csc.test.mocker;

import com.uaepay.pub.csc.datasource.facade.request.QueryDataRequest;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;
import com.uaepay.pub.csc.datasource.facade.response.QueryDataResponse;

/**
 * @author zc
 */
@Repository
public class QueryDataMocker {

    QueryDataResponse response;

    public QueryDataMocker fixFail(String message) {
        QueryDataResponse mock = new QueryDataResponse();
        mock.fail("MOCK_FAIL", message);
        this.response = mock;
        return this;
    }

    public QueryDataMocker fixSuccess(RowDataList rowDataList) {
        QueryDataResponse mock = new QueryDataResponse();
        mock.success(rowDataList);
        this.response = mock;
        return this;
    }

    public QueryDataResponse getResponse(QueryDataRequest request) {
        return response;
    }

}
