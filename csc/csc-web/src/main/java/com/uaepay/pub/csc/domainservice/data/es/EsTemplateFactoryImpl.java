//package com.uaepay.pub.csc.domainservice.data.es;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.stereotype.Service;
//
//import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
//import com.uaepay.pub.csc.domainservice.data.EsTemplateFactory;
//
///**
// * Elasticsearch客户端
// *
// * @author zc
// */
//@Service
//public class EsTemplateFactoryImpl implements EsTemplateFactory {
//
//    @Override
//    public RestHighLevelClient getOrCreate(EsDataSourceConfig dataSourceConfig) {
//        return dataSourceConfig.getRestHighLevelClient();
//    }
//
//}
