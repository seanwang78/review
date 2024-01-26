package com.uaepay.gateway.cgs.configuration;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.ufs2.client.common.enums.UfsReturnCode;
import com.uaepay.basis.ufs2.client.proxy.UfsImageClient;
import com.uaepay.basis.ufs2.client.proxy.configuration.UfsConfigContext;
import com.uaepay.basis.ufs2.client.proxy.configuration.UfsUserProperties;
import com.uaepay.basis.ufs2.client.proxy.share.UfsImageClientImpl;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "ufs2", name = "serviceUrl")
@EnableConfigurationProperties(UfsUserProperties.class)
public class UfsDefaultConfiguration {

    @Value("${spring.application.name}")
    String clientId;

    @Value("${ufs2.serviceUrl}")
    String url;

    @Autowired
    UesClientV2 uesClient;

    @Bean
    @Primary
    public UfsConfigContext ufsConfigContextDefault(ObjectProvider<UfsUserProperties> userPropertiesProvider) {
        UfsConfigContext.UfsConfigContextBuilder builder = UfsConfigContext.builder().clientId(clientId).url(url);
        UfsUserProperties userProperties = userPropertiesProvider.getIfAvailable();
        if (userProperties != null && userProperties.getUsername() != null) {
            if (userProperties.isNoDecrypt()) {
                log.info("ufs密码未加密");
            } else {
                EncryptContextV2 context = new EncryptContextV2();
                context.setTicket(userProperties.getPassword());
                boolean success = uesClient.getDataByTicket(context);
                if (success) {
                    userProperties.setPassword(context.getPlainData());
                } else {
                    throw new ErrorException(UfsReturnCode.CONFIG_ERROR, "ufs用户密码解密异常");
                }
            }
            builder.userProperties(userProperties);
        }
        return builder.build();
    }

    @Bean
    public UfsImageClient ufsImageClientDefault(@Qualifier("ufsConfigContextDefault")UfsConfigContext configContext) {
        UfsImageClientImpl ufsImageClient = new UfsImageClientImpl();
        ufsImageClient.setConfigContext(configContext);
        log.info("UfsImageClient(Default)自动配置完毕");
        return ufsImageClient;
    }

}
