package com.payby.gateway.openapi.common;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryOrderPageRequest implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1591338766667890928L;

    private Date startTime;

    private Date endTime;

    private PageParam pageParam;

}
