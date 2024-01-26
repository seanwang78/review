package com.uaepay.gateway.cgs.domainservice;

import com.uaepay.gateway.cgs.domain.key.DecryptKeyConfig;

import java.util.List;

public interface DecryptKeyService {

    List<DecryptKeyConfig> getKeyConfigs();

}
