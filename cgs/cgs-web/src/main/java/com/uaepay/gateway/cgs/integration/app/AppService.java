package com.uaepay.gateway.cgs.integration.app;

import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayResponse;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;

public interface AppService {

    ClientGatewayResponse doService(ReceiveOrderContext context);

}
