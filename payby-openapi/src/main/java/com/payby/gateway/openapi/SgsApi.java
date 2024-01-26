package com.payby.gateway.openapi;

import java.io.File;
import java.util.function.Predicate;

import com.alibaba.fastjson.TypeReference;
import com.payby.gateway.openapi.constant.Method;
import com.payby.gateway.openapi.response.ApplyProtocolResponse;
import com.payby.gateway.openapi.response.AuthTokenResponse;
import com.payby.gateway.openapi.response.CalculateFundoutResponse;
import com.payby.gateway.openapi.response.CryptoOrderResponse;
import com.payby.gateway.openapi.response.CryptoRefundOrderResponse;
import com.payby.gateway.openapi.response.GetAddressResponse;
import com.payby.gateway.openapi.response.GetCashierUrlInfoResult;
import com.payby.gateway.openapi.response.GetCustomerDepositOrderResponse;
import com.payby.gateway.openapi.response.GetFundoutAbilityListResponse;
import com.payby.gateway.openapi.response.GetPlaceOrderResponse;
import com.payby.gateway.openapi.response.GetProtocolResponse;
import com.payby.gateway.openapi.response.GetRefundOrderResponse;
import com.payby.gateway.openapi.response.GetSaveCardResponse;
import com.payby.gateway.openapi.response.GetTransferOrderResponse;
import com.payby.gateway.openapi.response.GetTransferToBankOrderResponse;
import com.payby.gateway.openapi.response.PlaceCryptoOrderResponse;
import com.payby.gateway.openapi.response.PlaceCryptoRefundOrderResponse;
import com.payby.gateway.openapi.response.PlaceOrderResponse;
import com.payby.gateway.openapi.response.PlaceRefundOrderResponse;
import com.payby.gateway.openapi.response.PlaceTransferOrderResponse;
import com.payby.gateway.openapi.response.PlaceTransferToBankOrderResponse;
import com.payby.gateway.openapi.response.QueryCustomerDepositOrderPageResponse;
import com.payby.gateway.openapi.response.ReceiptOrderResponse;

public enum SgsApi implements Api<SgsResponseWrap> {

    /** apply merchant user auth token api */
    APPLY_AUTH_TOKEN("/personal/apply/authToken", "v1", new TypeReference<SgsResponseWrap<AuthTokenResponse>>() {}),
    /** place acquire order **/
    PLACE_ACQUIRE_ORDER("/acquire2/placeOrder", "v1", new TypeReference<SgsResponseWrap<PlaceOrderResponse>>() {}),
    /** place refund order **/
    PLACE_REFUND_ORDER("/acquire2/refund/placeOrder", "v1",
        new TypeReference<SgsResponseWrap<PlaceRefundOrderResponse>>() {}),
    /** get acquire order **/
    GET_ACQUIRE_ORDER("/acquire2/getOrder", "v1", new TypeReference<SgsResponseWrap<GetPlaceOrderResponse>>() {}),
    /** get refund order **/
    GET_REFUND_ORDER("/acquire2/refund/getOrder", "v1",
        new TypeReference<SgsResponseWrap<GetRefundOrderResponse>>() {}),
    /** cancel acquire order **/
    CANCEL_ACQUIRE_ORDER("/acquire2/cancelOrder", "v1", new TypeReference<SgsResponseWrap<GetPlaceOrderResponse>>() {}),
    /** get transfer order **/
    GET_TRANSFER_ORDER("/transfer/getTransferOrder", "v1",
        new TypeReference<SgsResponseWrap<GetTransferOrderResponse>>() {}),
    /** get transfer bank order **/
    GET_TRANSFER_TO_BANK_ORDER("/transfer/getTransferToBankOrder", "v1",
        new TypeReference<SgsResponseWrap<GetTransferToBankOrderResponse>>() {}),
    /** place transfer order **/
    PLACE_TRANSFER_ORDER("/transfer/placeTransferOrder", "v1",
        new TypeReference<SgsResponseWrap<PlaceTransferOrderResponse>>() {}),
    /** place transfer bank order **/
    PLACE_TRANSFER_TO_BANK_ORDER("/transfer/placeTransferToBankOrder", "v1",
        new TypeReference<SgsResponseWrap<PlaceTransferToBankOrderResponse>>() {}),

    /** get order statement **/
    GET_ORDER_STATEMENT("/acquire2/download/getOrderStatement", "v1", new TypeReference<SgsResponseWrap<File>>() {}),

    /** get fund statement **/
    GET_FUND_STATEMENT("/acquire2/download/getFundStatement", "v1", new TypeReference<SgsResponseWrap<File>>() {}),

    /** revoke acquire order **/
    REVOKE_ACQUIRE_ORDER("/acquire2/revokeOrder", "v1", new TypeReference<SgsResponseWrap<GetPlaceOrderResponse>>() {}),

    /** apply protocol **/
    APPLY_PROTOCOL("/protocol/applyProtocol", "v1", new TypeReference<SgsResponseWrap<ApplyProtocolResponse>>() {}),

    /** get protocol **/
    GET_PROTOCOL("/protocol/getProtocol", "v1", new TypeReference<SgsResponseWrap<GetProtocolResponse>>() {}),

    /** echo **/
    ECHO("/verifySign", "v1", new TypeReference<String>() {}),

    /** get address **/
    GET_ADDRESS("/ccdeposit/getAddress", "v1", new TypeReference<SgsResponseWrap<GetAddressResponse>>() {}),

    /** get customer depositOrder **/
    GET_CUSTOMER_DEPOSIT_ORDER("/ccdeposit/getCustomerDepositOrder", "v1",
        new TypeReference<SgsResponseWrap<GetCustomerDepositOrderResponse>>() {}),

    /** query customer deposit order page **/
    QUERY_CUSTOMER_DEPOSIT_ORDER_PAGE("/ccdeposit/queryCustomerDepositOrderPage", "v1",
        new TypeReference<SgsResponseWrap<QueryCustomerDepositOrderPageResponse>>() {}),

    /** create digital receipt order **/
    CREATE_DIGITAL_RECEIPT_ORDER("/digitalReceipt/createOrder", "v1",
        new TypeReference<SgsResponseWrap<ReceiptOrderResponse>>() {}),

    /** get digital receipt order **/
    GET_DIGITAL_RECEIPT_ORDER("/digitalReceipt/getOrder", "v1",
        new TypeReference<SgsResponseWrap<ReceiptOrderResponse>>() {}),

    /** notify digital receipt order **/
    NOTIFY_DIGITAL_RECEIPT_ORDER("/digitalReceipt/notify", "v1",
        new TypeReference<SgsResponseWrap<ReceiptOrderResponse>>() {}),

    /** place crypto order **/
    PLACE_CRYPTO_ORDER("/crypto/placeOrder", "v1", new TypeReference<SgsResponseWrap<PlaceCryptoOrderResponse>>() {}),
    /** place crypto refund order **/
    PLACE_CRYPTO_REFUND_ORDER("/crypto/refund/placeOrder", "v1",
        new TypeReference<SgsResponseWrap<PlaceCryptoRefundOrderResponse>>() {}),
    /** get crypto order **/
    GET_CRYPTO_ORDER("/crypto/getOrder", "v1", new TypeReference<SgsResponseWrap<CryptoOrderResponse>>() {}),
    /** get crypto refund order **/
    GET_CRYPTO_REFUND_ORDER("/crypto/refund/getOrder", "v1",
        new TypeReference<SgsResponseWrap<CryptoRefundOrderResponse>>() {}),
    /** cancel crypto order **/
    CANCEL_CRYPTO_ORDER("/crypto/cancelOrder", "v1", new TypeReference<SgsResponseWrap<CryptoOrderResponse>>() {}),
    /** revoke crypto order **/
    REVOKE_CRYPTO_ORDER("/crypto/revokeOrder", "v1", new TypeReference<SgsResponseWrap<CryptoOrderResponse>>() {}),

    /** get fundout ability list **/
    GET_FUNDOUT_ABILITY_LIST("/transfer/getFundoutAbilityList", "v1",
        new TypeReference<SgsResponseWrap<GetFundoutAbilityListResponse>>() {}),

    /** calculate fundout **/
    CALCULATE_FUNDOUT("/transfer/calculateFundout", "v1",
        new TypeReference<SgsResponseWrap<CalculateFundoutResponse>>() {}),

    /** get acquire save card **/
    GET_ACQUIRE_SAVE_CARD("/acquire2/getSaveCard", "v1", new TypeReference<SgsResponseWrap<GetSaveCardResponse>>() {}),

    /** remove acquire save card **/
    REMOVE_ACQUIRE_SAVE_CARD("/acquire2/removeSaveCard", "v1", new TypeReference<SgsResponseWrap<Void>>() {}),

    /** get cashier url info **/
    GET_CASHIER_URL_INFO("/acquire2/getCashierUrlInfo", "v1",
        new TypeReference<SgsResponseWrap<GetCashierUrlInfoResult>>() {}),

    ;

    static final Predicate<SgsResponseWrap> checkResponse = wrap -> wrap.head.isSuccess();

    private String api;
    private String version;
    private Method method;
    private TypeReference responseType;

    SgsApi(String api, String version) {
        this(api, version, Method.POST);
    }

    SgsApi(String api, String version, Method method) {
        this(api, version, method, new TypeReference<SgsResponseWrap<Void>>() {});
    }

    SgsApi(String api, String version, TypeReference responseType) {
        this(api, version, Method.POST, responseType);
    }

    SgsApi(String api, String version, Method method, TypeReference responseType) {
        this.api = api;
        this.version = version;
        this.method = method;
        this.responseType = responseType;
    }

    @Override
    public String getApi() {
        return this.api;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public TypeReference getResponseType() {
        return this.responseType;
    }

    public static boolean checkResponse(SgsResponseWrap<?> wrap) {
        return checkResponse.test(wrap);
    }

}
