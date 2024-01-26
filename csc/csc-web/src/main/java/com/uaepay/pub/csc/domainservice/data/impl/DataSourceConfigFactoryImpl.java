package com.uaepay.pub.csc.domainservice.data.impl;

import java.util.*;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.stereotype.Repository;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.properties.DataSourceProperties;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.data.event.DataSourceRegisteredEvent;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 */
@Slf4j
@Repository
public class DataSourceConfigFactoryImpl
    implements DataSourceConfigFactory, ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

    private static final String JNDI_PREFIX = "java:comp/env/";

    @Autowired
    DataSourceProperties dataSourceProperties;

    private Environment environment;

    private Map<String, DataSourceConfig> codeSourceMap = new HashMap<>();

    @Override
    public DataSourceConfig getOrCreate(String code) {
        return codeSourceMap.get(code);
    }

    @Override
    public List<String> getCodeList() {
        List<String> result = new ArrayList<>();
        for (DataSourceConfig config : codeSourceMap.values()) {
            result.add(config.getCode());
        }
        return result;
    }

    @Override
    public Map<String, List<String>> getCodeMap() {
        Map<String, List<String>> result = new HashMap<>(8);
        for (DataSourceConfig config : codeSourceMap.values()) {
            if (config instanceof MySqlDataSourceConfig) {
                addToMapList(result, DataSourceTypeEnum.MYSQL.getCode(), config.getCode());
            } else if (config instanceof MongoDataSourceConfig) {
                addToMapList(result, DataSourceTypeEnum.MONGO.getCode(), config.getCode());
            } else if (config instanceof EsDataSourceConfig) {
                addToMapList(result, DataSourceTypeEnum.ES.getCode(), config.getCode());
            } else if (config instanceof ApiDataSourceConfig) {
                addToMapList(result, DataSourceTypeEnum.API.getCode(), config.getCode());
            }
        }
        return result;
    }

    private static <K, V> void addToMapList(Map<K, List<V>> result, K key, V value) {
        if (!result.containsKey(key)) {
            result.put(key, new ArrayList<>());
        }
        result.get(key).add(value);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        scanMysqlDataSource(context);
        registerMongoDataSource();
        registerEsDataSource();
        registerApiDataSource();
        context.publishEvent(new DataSourceRegisteredEvent(this));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void registerApiDataSource() {
        log.info("注册API数据源");
        ApiDataSourceConfig config = new ApiDataSourceConfig();
        codeSourceMap.put(DataSourceTypeEnum.API.getCode(), config);
    }

    private void scanMysqlDataSource(ApplicationContext context) {
        if (ArrayUtils.isEmpty(dataSourceProperties.getMysql())) {
            log.info("未配置mysql数据源");
            return;
        }
        for (String code : dataSourceProperties.getMysql()) {
            DataSource dataSource = searchMysqlDataSource(context, code);
            if (dataSource == null) {
                throw new ErrorException("mysql数据源加载异常: " + code);
            }
            log.info("注册mysql数据源: {}", code);
            codeSourceMap.put(code, new MySqlDataSourceConfig(code, dataSource));
        }
    }

    private DataSource searchMysqlDataSource(ApplicationContext context, String code) {
        DataSource dataSource = null;

        // 从bean中查找
        try {
            dataSource = context.getBean(code, DataSource.class);
        } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException e) {
        }

        // jndi查找
        if (dataSource == null) {
            JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
            jndi.setJndiName(JNDI_PREFIX + code);
            jndi.setExpectedType(DataSource.class);
            jndi.setLookupOnStartup(true);
            try {
                jndi.afterPropertiesSet();
            } catch (NamingException e) {
            }
            dataSource = (DataSource)jndi.getObject();
        }
        return dataSource;
    }

    private void registerMongoDataSource() {
        List<MongoDataSourceConfig> mongoConfigs = dataSourceProperties.getMongoConfigs();
        if (mongoConfigs == null) {
            return;
        }
        for (MongoDataSourceConfig config : mongoConfigs) {
            log.info("注册mongo数据源: {}", config.getCode());
            codeSourceMap.put(config.getCode(), config);
        }
    }

    private void registerEsDataSource() {
        // String code = dataSourceProperties.getEsBusiness();
        // log.info("注册elasticsearch数据源: {}", code);
        String prefix = "elasticsearch.datasource.";
        List<String> esDataSourceCodes = getEsDataSourceNames(environment, prefix);
        if (esDataSourceCodes.size() > 0) {
            for (String name : esDataSourceCodes) {
                EsDataSourceConfig esDataSourceConfig = getEsDataSourceConfig(environment, prefix, name);
                codeSourceMap.put(esDataSourceConfig.getCode(), esDataSourceConfig);
            }
        }
    }

    private List<String> getEsDataSourceNames(final Environment environment, final String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment)environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return Arrays.asList(standardEnv.getProperty(prefix + "names").split(","));
    }

    private EsDataSourceConfig getEsDataSourceConfig(Environment environment, String prefix, String dataSourceName) {
        StandardEnvironment standardEnv = (StandardEnvironment)environment;
        prefix = prefix + dataSourceName + ".";
        String uris = standardEnv.getProperty(prefix + "uris");
        String schema = standardEnv.getProperty(prefix + "schema");
        String charset = standardEnv.getProperty(prefix + "charset");
        String connectTimeOut = standardEnv.getProperty(prefix + "connectTimeOut");
        String socketTimeout = standardEnv.getProperty(prefix + "socketTimeout");
        String username = standardEnv.getProperty(prefix + "username");
        String password = standardEnv.getProperty(prefix + "password");

        String[] hosts = uris.split(",");

        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String[] ipPort = hosts[i].split(":");
            httpHosts[i] = new HttpHost(ipPort[0], Integer.parseInt(ipPort[1]), schema);
        }

        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

        Header[] defaultHeaders = new Header[] {new BasicHeader("Accept", "*/*"), new BasicHeader("Charset", charset),};
        restClientBuilder.setDefaultHeaders(defaultHeaders);
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                log.info("监听某个es节点失败");
            }
        });
        restClientBuilder.setRequestConfigCallback(builder -> builder
            .setConnectTimeout(Integer.parseInt(connectTimeOut)).setSocketTimeout(Integer.parseInt(socketTimeout)));

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }

        return new EsDataSourceConfig(dataSourceName, new RestHighLevelClient(restClientBuilder));
    }
}
