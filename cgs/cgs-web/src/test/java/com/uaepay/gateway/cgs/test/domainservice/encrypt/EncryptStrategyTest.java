package com.uaepay.gateway.cgs.test.domainservice.encrypt;

import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import com.uaepay.gateway.cgs.domainservice.encrypt.strategy.DefaultEncryptStrategy;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/8/11
 * @since 0.1
 */
public class EncryptStrategyTest extends ApplicationTestBase {


    @Autowired
    private DefaultEncryptStrategy defaultEncryptStrategy;

    @Test
    public void test() {
        defaultEncryptStrategy.convert("name", "Y74hVwfSM4DcQFs/nSazUtYSHVAYOrzjUdRxW6+qTl18vjOe/KkX/Zl/EiT1I5/cE2z2HGn3GvkU2VcpvvCJ+ds5U+AniiN2EUmptYIeAyWq2IIqd9kI/FkvU3+tQKtx2AFrb4vyvLA4k6Fp0N/gaFDD5KXiBV9ULcD0esrJ8k6NxXxiKYWO42XCAPhq1DCoNyFMkEJkJxXUuYu+Dn1WYTgmqMaEc/1LM+ond590JYvW9tkz0NjETLqUbWM5a8uK3FEo9RmTvSeOM02KO6klFR6S3BhpMyEKNf5DbOjYcjCypd+UgOT4nf54Iu5kleUswMaMul6V8N/81OdgnPmQKg==", new ContextParameter("VvAZCeQA7Duf7kBX8g5iew==", null));
        defaultEncryptStrategy.convert("passportNo", "oJW8g2p5AAsTq7VlZ680myKVoz6\\/X1z8qTCMb8+RNsFnrHjnTT8s6lbERT4plQd485miBx8cfJlQ10ObPg9dwgYmdJmlbIHtS\\/\\/0sUD+hOh53X2C22Mtsxc5A5hmkBkGdBtn9j00zZSSbo5MNQMQX5IBpOu02e5b2f\\/os17bwcbNVSI62u\\/v3XKv1iqDl2cd1qEHvPiCRqV22NmbsVLWMqAiujI5KqaIBs4w5amjFBjdKrktcgrdjiNPDTWLaFGjrvRgky8ksW54SVlh3\\/1DiPWNhAWxh7Vv4Av6gU5MKBPjEJZEYOIM\\/uby1rAchZFgZfT6wczwbP3BSYbZdLcpJA==", new ContextParameter("VvAZCeQA7Duf7kBX8g5iew==", null));
    }




}
