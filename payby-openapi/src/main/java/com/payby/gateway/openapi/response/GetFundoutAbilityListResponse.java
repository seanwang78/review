package com.payby.gateway.openapi.response;

import java.io.Serializable;
import java.util.List;

import com.payby.gateway.openapi.model.FundoutAbility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetFundoutAbilityListResponse implements Serializable {
    private static final long serialVersionUID = 4317629852561460030L;

    private List<FundoutAbility> fundoutAbilityList;

}