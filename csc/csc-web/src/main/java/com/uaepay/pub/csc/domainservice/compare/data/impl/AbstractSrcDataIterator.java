package com.uaepay.pub.csc.domainservice.compare.data.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.QuerySplitParam;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;

/**
 * 抽象源数据查询迭代器
 * 
 * @author zc
 */
public abstract class AbstractSrcDataIterator implements SrcDataIterator {

    public AbstractSrcDataIterator(String sqlTemplate, int splitMinutes, String relateField, int pageSize,
        QueryParam queryParam) {
        this.sqlTemplate = sqlTemplate;
        this.splitMinutes = splitMinutes;
        this.relateField = relateField;
        this.pageSize = pageSize;
        this.queryParam = queryParam;
        this.init();
        this.splitParamsIterator = splitQueryParam().iterator();
    }

    protected String sqlTemplate;
    protected int splitMinutes;
    protected String relateField;
    protected int pageSize;
    protected QueryParam queryParam;

    /** 切分后查询参数的迭代器 */
    private Iterator<QuerySplitParam> splitParamsIterator;
    /** 当前在处理的查询参数 */
    private QuerySplitParam currentSplitParam;
    /** 当前查询参数的分页查询进度，从1开始 */
    private int currentPageProgress;

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 切分查询参数
     * 
     * @return 切分后参数
     */
    protected abstract List<QuerySplitParam> splitQueryParam();

    @Override
    public boolean hasNext() {
        if (currentSplitParam == null) {
            if (splitParamsIterator == null || !splitParamsIterator.hasNext()) {
                return false;
            }
            // 初始化当前进度
            currentSplitParam = splitParamsIterator.next();
            currentPageProgress = 0;
        }
        return true;
    }

    @Override
    public SrcRows next() {
        currentPageProgress++;
        SrcRows query = query(currentSplitParam, currentPageProgress, pageSize);
        // 如果查询不到数据，则重置当前查询参数
        if (query.size() < pageSize) {
            currentSplitParam = null;
        }
        return query;
    }

    /**
     * 查询指定页数据
     * 
     * @param querySplitParam
     *            查询参数
     * @param page
     *            页码
     * @return
     */
    protected abstract SrcRows query(QuerySplitParam querySplitParam, int page, int pageSize);

}
