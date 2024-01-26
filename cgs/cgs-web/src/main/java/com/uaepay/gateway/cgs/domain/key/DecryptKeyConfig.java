package com.uaepay.gateway.cgs.domain.key;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Date;

import lombok.Data;

@Data
public class DecryptKeyConfig implements Serializable {

    private long configId;

    private String privateKey;

    private Date gmtEffect;

    private Date gmtExpired;

    private int keySize;

    public boolean isEffect() {
        Date now = new Date();
        return gmtEffect.compareTo(now) <= 0 && now.compareTo(gmtExpired) <= 0;
    }

}
