package com.uaepay.pub.csc.domain.data;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 */
@Slf4j
public class RowDataConverter {

    public static List<RowData> from(SqlRowSet rowSet, ColumnData columnData) {
        List<RowData> rows = new ArrayList<>();
        while (rowSet.next()) {
            List<Object> values = new ArrayList<>();
            for (int i = 1; i <= columnData.getColumnInfos().size(); i++) {
                values.add(rowSet.getObject(i));
            }
            rows.add(new RowData(columnData, values));
        }
        return rows;
    }

    public static List<RowData> fromRowDataList(RowDataList rowDataList, ColumnData columnData) {
        List<RowData> rows = new ArrayList<>();
        if (rowDataList == null || CollectionUtils.isEmpty(rowDataList.getRowList())) {
            return rows;
        }
        for (List<Object> rowData : rowDataList.getRowList()) {
            rows.add(new RowData(columnData, rowData));
        }
        return rows;
    }

    public static List<RowData> fromMongo(List<Document> batch, ColumnData columnData) {
        List<RowData> rows = new ArrayList<>();
        for (Document doc : batch) {
            List<Object> values = new ArrayList<>(Collections.nCopies(columnData.getColumnInfos().size(), null));
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                String key = StringUtils.lowerCase(entry.getKey());
                values.set(columnData.getIndex(key), convertValue(entry.getValue()));
            }
            rows.add(new RowData(columnData, values));
        }
        return rows;
    }

    public static List<RowData> fromMongoAgg(List<Map<String, Object>> batch, ColumnData columnData) {
        List<RowData> rows = new ArrayList<>();
        for (Map<String, Object> doc : batch) {
            List<Object> values = new ArrayList<>(Collections.nCopies(columnData.getColumnInfos().size(), null));
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                String key = StringUtils.lowerCase(entry.getKey());
                values.set(columnData.getIndex(key), convertValue(entry.getValue()));
            }
            rows.add(new RowData(columnData, values));
        }
        return rows;
    }

    public static List<RowData> fromEs(SearchHit[] batch, ColumnData columnData) {
        List<RowData> rows = new ArrayList<>();
        int size = columnData.getColumnInfos().size();
        for (SearchHit hit : batch) {
            List<Object> values = new ArrayList<>(size);
            values.add(columnData.getIndex("_id"), hit.getId());
            for (Map.Entry<String, Object> entry : hit.getSourceAsMap().entrySet()) {
                String key = StringUtils.lowerCase(entry.getKey());
                values.add(columnData.getIndex(key), convertValue(entry.getValue()));
            }
            rows.add(new RowData(columnData, values));
        }
        return rows;
    }

    public static List<RowData> fromEs(Aggregations aggregations, ColumnData columnData) {
        List<RowData> rowList = new ArrayList<>();
        if (aggregations == null) {
            return rowList;
        }
        parseAggregations(aggregations, new HashMap<>(), rowList, columnData);
        return rowList;
    }

    private static RowData convertRowData(Map<String, Object> item, ColumnData columnData) {
        List<Object> rowData = new ArrayList<>(columnData.getColumnInfos().size());
        for (ColumnData.Column column : columnData.getColumnInfos()) {
            Object value = item.get(column.getName());
            if (value instanceof Double) {
                rowData.add(columnData.getIndex(column.getName()), new BigDecimal(value.toString()));
            } else {
                rowData.add(columnData.getIndex(column.getName()), value);
            }
        }

        return new RowData(columnData, rowData);
    }

    public static void parseAggregations(Aggregations aggregations, Map<String, Object> row, List<RowData> rowList,
        ColumnData columnData) {
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        Set<String> aggNames = asMap.keySet();
        boolean leaf = false;
        for (String aggName : aggNames) {
            Aggregation aggregation = asMap.get(aggName);
            if (!(aggregation instanceof ParsedTerms)) {
                leaf = true;
            }
            if (aggregation instanceof ParsedTerms) {
                List<? extends Bucket> parsedTermsList = ((ParsedTerms)aggregation).getBuckets();
                for (int i = 0; i < parsedTermsList.size(); i++) {
                    Map<String, Object> newRow = new HashMap<>(row);
                    Bucket item = parsedTermsList.get(i);
                    newRow.put(aggName, item.getKeyAsString() == null ? item.getKey() : item.getKeyAsString());
                    newRow.put(Constants.COLUMN_COUNT, item.getDocCount());
                    if (item.getAggregations().asList().size() > 0) {
                        parseAggregations(item.getAggregations(), newRow, rowList, columnData);
                    } else {
                        rowList.add(convertRowData(newRow, columnData));
                    }
                }
            } else if (aggregation instanceof ParsedAvg) {
                row.put(aggName, convertToBigDecimalOrString(((ParsedAvg)aggregation).getValueAsString()));
            } else if (aggregation instanceof ParsedSum) {
                row.put(aggName, convertToBigDecimalOrString(((ParsedSum)aggregation).getValueAsString()));
            } else if (aggregation instanceof ParsedMin) {
                row.put(aggName, convertToBigDecimalOrString(((ParsedMin)aggregation).getValueAsString()));
            } else if (aggregation instanceof ParsedMax) {
                row.put(aggName, convertToBigDecimalOrString(((ParsedMax)aggregation).getValueAsString()));
            } else if (aggregation instanceof ParsedValueCount) {
                row.put(aggName, ((ParsedValueCount)aggregation).getValue());
            } else if (aggregation instanceof ParsedFilter) {
                row.put(aggName, ((ParsedFilter)aggregation).getDocCount());
            } else if (aggregation instanceof ParsedSimpleValue) {
                row.put(aggName, convertToBigDecimalOrString(((ParsedSimpleValue)aggregation).getValueAsString()));
            }
            else {
                log.warn("unused agg result: {}", aggregation.getClass());
            }
        }
        if (leaf) {
            rowList.add(convertRowData(row, columnData));
        }
    }

    private static Object convertToBigDecimalOrString(String result) {
        try {
            return new BigDecimal(result);
        } catch (Exception e) {
            return result;
        }
    }

    private static Object convertValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Decimal128) {
            return ((Decimal128)value).bigDecimalValue();
        } else if (value instanceof Double) {
            return BigDecimal.valueOf((double)value);
        } else if (!(value instanceof String) && !(value instanceof Long)) {
            String s = null;
        }
        return value;
    }

}
