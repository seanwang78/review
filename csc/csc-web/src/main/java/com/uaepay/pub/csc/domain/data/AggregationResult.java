package com.uaepay.pub.csc.domain.data;

import lombok.Data;
import org.elasticsearch.search.aggregations.Aggregations;

@Data
public class AggregationResult {
    private String value;
    private Long count;
    private Aggregations aggregations;
}
