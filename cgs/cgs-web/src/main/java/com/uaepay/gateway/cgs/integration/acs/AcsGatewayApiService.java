package com.uaepay.gateway.cgs.integration.acs;

import com.uaepay.gateway.cgs.domain.api.ApiConfig;

public interface AcsGatewayApiService {

    ApiConfig getApiConfig(String apiCode);

}
