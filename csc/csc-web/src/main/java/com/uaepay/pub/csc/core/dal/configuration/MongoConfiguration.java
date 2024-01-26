//package com.uaepay.pub.csc.core.dal.configuration;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.bson.types.Decimal128;
//import org.springframework.beans.BeanUtils;
//import org.springframework.boot.autoconfigure.domain.EntityScanner;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.boot.context.properties.PropertyMapper;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.annotation.Persistent;
//import org.springframework.data.convert.ReadingConverter;
//import org.springframework.data.convert.WritingConverter;
//import org.springframework.data.mapping.model.FieldNamingStrategy;
//import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//
///**
// * @author zc
// */
//@Configuration(proxyBeanMethods = false)
//public class MongoConfiguration {
//
//    /**
//     * 参考：{@link org.springframework.boot.autoconfigure.data.mongo.MongoDataConfiguration}
//     */
//    @Bean
//    MongoMappingContext mongoMappingContext(ApplicationContext applicationContext, MongoProperties properties,
//        MongoCustomConversions conversions) throws ClassNotFoundException {
//        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
//        MongoMappingContext context = new MongoMappingContext();
//        mapper.from(properties.isAutoIndexCreation()).to(context::setAutoIndexCreation);
//        context.setInitialEntitySet(new EntityScanner(applicationContext).scan(Document.class, Persistent.class));
//        Class<?> strategyClass = properties.getFieldNamingStrategy();
//        if (strategyClass != null) {
//            context.setFieldNamingStrategy((FieldNamingStrategy)BeanUtils.instantiateClass(strategyClass));
//        }
//        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
//        return context;
//    }
//
//    @Bean
//    public MongoCustomConversions mongoCustomConversions() {
//        List<Converter<?, ?>> list = new ArrayList<>();
//        list.add(new BigDecimalToDecimal128Converter());
//        list.add(new Decimal128ToBigDecimalConverter());
//        MongoCustomConversions result = new MongoCustomConversions(list);
//        return result;
//    }
//
//    @ReadingConverter
//    @WritingConverter
//    public static class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
//        @Override
//        public Decimal128 convert(BigDecimal bigDecimal) {
//            return new Decimal128(bigDecimal);
//        }
//    }
//
//    @ReadingConverter
//    @WritingConverter
//    public static class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
//        @Override
//        public BigDecimal convert(Decimal128 decimal128) {
//            return decimal128.bigDecimalValue();
//        }
//    }
//}
