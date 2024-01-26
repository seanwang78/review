package com.uaepay.pub.csc.domain.data;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketScriptPipelineAggregationBuilder;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;
import com.uaepay.pub.csc.domainservice.monitor.data.es.EsQueryDataIteratorFactory;

import lombok.Data;

/**
 * 列数据
 * 
 * @author zc
 */
public class ColumnData {

    public static ColumnData from(SqlRowSetMetaData metaData, String relateField) {
        return from(metaData, relateField, true);
    }

    public static ColumnData from(SqlRowSetMetaData metaData, String relateField, boolean lowerCaseName) {
        List<Column> columns = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            int index = i - 1;
            String name = metaData.getColumnLabel(i);
            if (lowerCaseName) {
                name = name.toLowerCase();
            }
            columns.add(new Column(index, name));
        }
        return new ColumnData(columns, relateField);
    }

    public static ColumnData fromRowDataList(RowDataList rowDataList, String relateField, boolean lowerCaseName) {
        List<Column> columns = new ArrayList<>();
        if (rowDataList != null && CollectionUtils.isNotEmpty(rowDataList.getColumnNames())) {
            for (int i = 0; i != rowDataList.getColumnNames().size(); i++) {
                String name = rowDataList.getColumnNames().get(i);
                if (lowerCaseName) {
                    name = name.toLowerCase();
                }
                columns.add(new Column(i, name));
            }
        }
        return new ColumnData(columns, relateField);
    }

    public static ColumnData fromMongo(List<Document> batch, String relateField) {
        Map<String, Column> columnMap = new LinkedHashMap<>();
        for (Document doc : batch) {
            for (String key : doc.keySet()) {
                if (!columnMap.containsKey(key)) {
                    columnMap.put(key, new Column(columnMap.size(), key));
                }
            }
        }
        return new ColumnData(new ArrayList<>(columnMap.values()), relateField);
    }

    public static ColumnData fromMongoAgg(List<Map<String, Object>> batch, String relateField) {
        Map<String, Column> columnMap = new LinkedHashMap<>();
        for (Map<String, Object> doc : batch) {
            for (String key : doc.keySet()) {
                if (!columnMap.containsKey(key)) {
                    columnMap.put(key, new Column(columnMap.size(), key));
                }
            }
        }
        return new ColumnData(new ArrayList<>(columnMap.values()), relateField);
    }

    public static ColumnData fromEs(SearchHit[] batch, String relateField) {
        Map<String, Column> columnMap = new LinkedHashMap<>();
        if (batch.length > 0) {
            columnMap.put("_id", new Column(0, "_id"));
        }
        for (SearchHit hit : batch) {
            Map<String, Object> fields = hit.getSourceAsMap();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                if (!columnMap.containsKey(entry.getKey())) {
                    columnMap.put(entry.getKey(), new Column(columnMap.size(), entry.getKey()));
                }
            }
        }
        return new ColumnData(new ArrayList<>(columnMap.values()), relateField);
    }

    public static ColumnData fromEs(EsQueryDataIteratorFactory.EsQueryDataIterator.AggParseResult aggParseResult,
        String relateField) {
        Map<String, Column> columnMap = new LinkedHashMap<>();
        parseColumnName(aggParseResult.getAggBuilders(), columnMap);
        return new ColumnData(new ArrayList<>(columnMap.values()), relateField);
    }

    public static void parseColumnName(Collection<AggregationBuilder> list, Map<String, Column> columnMap) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (AggregationBuilder aggregation : list) {
            String name = aggregation.getName();
            Collection<AggregationBuilder> subAggregations = aggregation.getSubAggregations();
            if (aggregation instanceof TermsAggregationBuilder) {
                checkAndFillColumn(columnMap, name);
                if (CollectionUtils.isEmpty(subAggregations)
                    || !(aggregation.getSubAggregations().iterator().next() instanceof TermsAggregationBuilder)) {
                    checkAndFillColumn(columnMap, Constants.COLUMN_COUNT);
                }
            } else {
                checkAndFillColumn(columnMap, name);
            }
            parseColumnName(subAggregations, columnMap);
            parsePipelineColumnName(aggregation.getPipelineAggregations(), columnMap);
        }
    }

    public static void parsePipelineColumnName(Collection<PipelineAggregationBuilder> list,
        Map<String, Column> columnMap) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (PipelineAggregationBuilder aggregation : list) {
            String name = aggregation.getName();
            if (aggregation instanceof BucketScriptPipelineAggregationBuilder) {
                checkAndFillColumn(columnMap, name);
            }
        }
    }

    private static void checkAndFillColumn(Map<String, Column> columnMap, String name) {
        ParameterValidate.assertTrue("Duplicate column name:" + name, !columnMap.containsKey(name));
        columnMap.put(name, new Column(columnMap.size(), name));
    }

    public ColumnData(List<Column> columns, String relateField) {
        this.columnInfos = columns;
        this.nameIndexMap = new HashMap<>(columns.size());
        for (Column column : columns) {
            this.nameIndexMap.put(StringUtils.lowerCase(column.getName()), column);
        }
        String tempRelateField = StringUtils.lowerCase(relateField);
        if (StringUtils.isNotBlank(tempRelateField) && columns.size() != 0) {
            Column relateColumn = nameIndexMap.get(tempRelateField);
            if (relateColumn == null) {
                throw new InvalidParameterException("关联列不存在: " + relateField);
            }
            this.relateFieldIndex = relateColumn.getIndex();
        } else {
            this.relateFieldIndex = 0;
        }
    }

    /** 列信息 */
    private List<Column> columnInfos;

    /** 列名索引 */
    private Map<String, Column> nameIndexMap;

    /** 关联列索引 */
    private int relateFieldIndex;

    /**
     * 判断是否存在列
     * 
     * @param columnName
     *            列名
     * @return 是否存在
     */
    public boolean containsColumn(String columnName) {
        return nameIndexMap.get(StringUtils.lowerCase(columnName)) != null;
    }

    /**
     * 获取列
     * 
     * @param name
     *            列名
     * @return 列索引
     * @throws InvalidParameterException
     *             如果不存在抛异常
     */
    public int getIndex(String name) {
        Column column = nameIndexMap.get(StringUtils.lowerCase(name));
        if (column == null) {
            throw new InvalidParameterException("列不存在: " + name);
        }
        return column.getIndex();
    }

    /**
     * 获取关联列索引
     * 
     * @return 关联列索引
     */
    public int getRelateFieldIndex() {
        return relateFieldIndex;
    }

    public List<Column> getColumnInfos() {
        return columnInfos;
    }

    /**
     * 拼接所有的列
     * 
     * @param separator
     *            分隔符
     */
    public String joinColumn(CharSequence separator) {
        return columnInfos.stream().map(Column::getName).collect(Collectors.joining(separator));
    }

    @Override
    public String toString() {
        return "ColumnData{" + "columnInfos=" + columnInfos + ", relateFieldIndex=" + relateFieldIndex + '}';
    }

    @Data
    public static class Column {

        /** 索引，从0开始 */
        int index;

        /** 列名 */
        String name;

        public Column(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

}
