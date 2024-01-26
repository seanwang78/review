package com.uaepay.pub.csc.core.dal.configuration;

import javax.sql.DataSource;

import com.uaepay.basis.sequenceutil.IDGen;
import com.uaepay.basis.sequenceutil.segment.SegmentIDGenImpl;
import com.uaepay.basis.sequenceutil.segment.dao.IDAllocDao;
import com.uaepay.basis.sequenceutil.segment.dao.impl.IDAllocDaoImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Mapper配置
 *
 * @author zc
 */
@Configuration
@MapperScan(basePackages = {"com.uaepay.pub.csc.core.dal"}, sqlSessionFactoryRef = "sqlSessionFactory")
@ImportResource(locations = "classpath:META-INF/spring/datasource-csc.xml")
public class CscMapperConfiguration {

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("cscDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setConfigLocation(new ClassPathResource("META-INF/mybatis-config.xml"));
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:META-INF/sqlmap/**/*Mapper.xml"));
        return sessionFactory.getObject();
    }

    @Bean("transactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("cscDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("transactionTemplate")
    public TransactionTemplate transactionTemplate(
        @Qualifier("transactionManager") DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }

    @Bean
    public IDAllocDao idAllocDao(@Qualifier("cscDataSource") DataSource dataSource) {
        return new IDAllocDaoImpl(dataSource);
    }

    @Bean
    public IDGen idGen(IDAllocDao idAllocDao) {
        SegmentIDGenImpl result = new SegmentIDGenImpl();
        result.setDao(idAllocDao);
        return result;
    }

}
