package com.uaepay.gateway.cgs.test.tool;

import com.uaepay.acs.service.facade.key.KeyConfigManageFacade;
import com.uaepay.acs.service.facade.key.domain.KeyConfig;
import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.basis.beacon.service.facade.domain.request.ObjectOperateRequest;
import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.CgsApplication;
import com.uaepay.gateway.cgs.integration.BaseService;
import com.uaepay.gateway.cgs.integration.acs.impl.AcsKeyConfigServiceImpl;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CgsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Ignore
public class AppKeyGenerator {

    protected static final String CLIENT_ID = "cgs_unit_test";

    @Reference
    KeyConfigManageFacade keyConfigManageFacade;

    @Autowired
    UesClientV2 uesClientV2;

    @Test
    public void generate() throws Exception {
        KeyConfig config = new KeyConfig();
        config.setAlgorithm(AcsKeyConfigServiceImpl.ALGORITHM);
        config.setCertType(AcsKeyConfigServiceImpl.CERT_TYPE);
        config.setEnableFlag(YesNoEnum.YES);
        config.setGatewayType(BaseService.GATEWAY_TYPE);
        config.setGmtEffect(new DateTime().toDate());
        config.setGmtExpired(new DateTime().plusYears(1).toDate());
        config.setPartnerId(AcsKeyConfigServiceImpl.PARTNER_ID);

        // buildFromApi(config);
        buildFromOpenssl(config);

        ObjectOperateRequest<KeyConfig> request = new ObjectOperateRequest<>();
        request.setClientId(CLIENT_ID);
        request.setEntity(config);
        CommonResponse response = keyConfigManageFacade.save(request);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            throw new RuntimeException(String.format("保存失败: %s, %s", response.getCode(), response.getMessage()));
        }
        System.out.println("保存成功！");
    }

    /**
     * API构建
     */
    private void buildFromApi(KeyConfig config) throws Exception {
        RsaUtil.RsaKeyPair keyPair = RsaUtil.generateKeyPair(2048);
        config.setCert(encrypt(keyPair.getPrivateKey()));
        config.setRelateCert(keyPair.getPublicKey());
        config.setMemo("Create by RsaUtil");
    }

    private void buildFromOpenssl(KeyConfig config) throws Exception {
        String privateKey =
            "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDJ/yVk8mY5ylY6wH6qHSEHIfhbv5yZkMSsyXIroNEAg5hYT8EVdeE9j5DYz06105eS9PHGnRofyEL05HlLQoKCsM6H1rWA7I4skQJCUgmHhqZzk53/rIQ9tIM26Lgm1VAdiF/EPshYfMsFJHzRndcQevztEO9BjXJ5LTBit71MJJQMtQyUZErgsuKDNId0yxXF5YiXCCHOPi0zulGmmRDpfKCz1XQa6ctE3A+d4JJnYraaFbLLc6iFUb5Knz4Gtgcw0jpgTY31Iu7R7+6QB80oVFMkxyFzi3saojUEFesde+iIaxg1YMvmAP6/l2LDD80mhxQbrlSqX76cOobSY2chAgMBAAECggEBAI6cwwv4T9AxWJv2V/dGxZDBnRU5vRh2q0TKnP7MdYMFZcPD2zCJn1LVT5CjHJEnKHaq1SBCvrT9sLgxrqlB9d7LcaVaI584ZFB2uL+WKAA1QsOKzxw58suV7KYRvCI+rGeNZv+oy84dnUCuOCnOZI7QJk5BjIodkVqYWvb3foCIeZvGptHTZftQvIHAD4hFr7g+55/NalqCemBb73N3uQxy2G+UmMhUDwjuLbpj9u3o0SMzbwB1yOku5mwpxPdYxJnI+uPYRAUMHfKPSnEhnLpP7Rp28W4idOaojulOsHKxAfNtbw7JykoQmnySH7adiqvKZaCBCrJuwW1d4CpJm6ECgYEA5FkuIWE2Bbtz/2EP5ieFp/nXVCYqj0eIY3F4HVsGeeMruqDfNcM8X0Z92yNdCAmq+eDUgI2/QdY/Z9SzL05Zqh18CpEaUzCgIde42hNsFZR8CXW/J6pjKg6se6/sdNjrXE7oGH1xB3yv/NDfzX2FiLWfzgt67eos6GIFt1Qp+f0CgYEA4nUPmKjqkpEV/X0ZJeilYLT91veBLLliq5yfiBjxB++ZYkJ4q56/EyeJM6rqfIjw36KZuBFOPKmx4dA/o9JHEuleGxzDRQ0fNLDBb/Ojr14KdkOvQ6n/aa7yRh2cgAfwmop+CfPt0FGTdru2ghdQCJ0X0jmMfneOg+CaEsgISPUCgYEAka1bQqxT3KBURm5TyE8ac6lueB7JNBM8rcRsDr5NiWEmOq/69r+ROm8sR6tt5HzWeQe4SjMj+wqF9OW7Usi3Z1HcGpmro7r3zd9j7KxLXhEWeKqHwvRuwxwNafM39OuWa5Njeow8mbwwQie3P89+a1MDml20lSe6cp5mayjkhrkCgYEAmR29ZeMQd/lRfQvQrSkYaML3vxmqZasTZCWKWGNE1bDTbPS00pWLbFAXHLA7y+hrtUoj1akd1TaH577+yK2tEMRptLtF7LJqnx3ELQX3buOO/1fUcVPINpTriy0KXCcCL/vkLKaZ7Xi61FUnmd5ZZvmQ2bu/muMLoW+DvZ8yuuECgYBS87OMj+YYp7JZ0WH5TPgJItD54lqOhMZrsTpO8nyy+veKX7cy16nwPOC1wv2iHS8BtomEmgId9u4Q7cb9rtevmi33t1BtV0xEz0e+rNtcakrCgCu12gHX32l9ATKUx+T117A+lnyTI2Q8gzUMQ36CHxB0zeOMFYaN4jF8bvLdOQ==";
        String publicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyf8lZPJmOcpWOsB+qh0hByH4W7+cmZDErMlyK6DRAIOYWE/BFXXhPY+Q2M9OtdOXkvTxxp0aH8hC9OR5S0KCgrDOh9a1gOyOLJECQlIJh4amc5Od/6yEPbSDNui4JtVQHYhfxD7IWHzLBSR80Z3XEHr87RDvQY1yeS0wYre9TCSUDLUMlGRK4LLigzSHdMsVxeWIlwghzj4tM7pRppkQ6Xygs9V0GunLRNwPneCSZ2K2mhWyy3OohVG+Sp8+BrYHMNI6YE2N9SLu0e/ukAfNKFRTJMchc4t7GqI1BBXrHXvoiGsYNWDL5gD+v5diww/NJocUG65Uql++nDqG0mNnIQIDAQAB";

        config.setCert(encrypt(privateKey));
        config.setRelateCert(publicKey);
        config.setMemo("Create by Openssl");
    }

    private String encrypt(String plain) throws Exception {
        EncryptContextV2 contextV2 = new EncryptContextV2();
        contextV2.setPlainData(plain);
        contextV2.setDataType("cgs_common_data");

        uesClientV2.saveData(contextV2);
        return contextV2.getTicket();
    }

}
