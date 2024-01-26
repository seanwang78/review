package com.payby.gateway.openapi.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SortPageParam extends PageParam {

    /**
     *
     */
    private static final long serialVersionUID = 8043931336848107546L;

    private List<SortParam> sortParamList;

    /**
     * 
     * @param number
     * @param size
     */
    public SortPageParam(int number, int size) {
        super(number, size);
    }

    public SortPageParam(PageParam pageParam, SortParam sortParam) {
        this(pageParam.getNumber(), pageParam.getSize(), sortParam);
    }

    public SortPageParam(int number, int size, SortParam sortParam) {
        super(number, size);
        List<SortParam> list = new ArrayList<SortParam>();
        list.add(sortParam);
        this.sortParamList = list;
    }

    public SortPageParam(int number, int size, List<SortParam> sortParamList) {
        super(number, size);
        this.sortParamList = sortParamList;
    }

}
