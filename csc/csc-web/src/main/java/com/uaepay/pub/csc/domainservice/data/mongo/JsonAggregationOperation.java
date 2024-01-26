package com.uaepay.pub.csc.domainservice.data.mongo;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author zc
 */
public class JsonAggregationOperation implements AggregationOperation {

    public JsonAggregationOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }

    private final String jsonOperation;

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return null;
    }

    @Override
    public List<Document> toPipelineStages(AggregationOperationContext context) {
        org.bson.Document document = org.bson.Document.parse(jsonOperation);
        return (List<Document>)document.get("pipeline");
    }

}
