package com.uaepay.pub.csc.datasource.facade.response;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;

/**
 * 数据查询响应
 * 
 * @author zc
 */
public class QueryDataResponse extends CommonResponse {

    private static final long serialVersionUID = 1L;

    RowDataList rowDataList;

    public QueryDataResponse success(RowDataList rowDataList) {
        super.success();
        this.rowDataList = rowDataList;
        return this;
    }

    public RowDataList getRowDataList() {
        return rowDataList;
    }

    public void setRowDataList(RowDataList rowDataList) {
        this.rowDataList = rowDataList;
    }

}
