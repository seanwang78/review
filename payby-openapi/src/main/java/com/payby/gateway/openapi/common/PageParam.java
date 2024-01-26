package com.payby.gateway.openapi.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageParam implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1692679838910989381L;

    private int number;

    private int size;

}