package com.payby.gateway.openapi.common;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Pagination<T> implements Serializable {

    private long totalElements;

    private long totalPages;

    private SortPageParam sortPageParam;

    private List<T> items;

    public <U> Pagination<U> map(Function<? super T, ? extends U> converter) {
        Pagination<U> pagination = new Pagination<>();
        List<U> convertedContent = getConvertedContent(converter);
        pagination.setItems(convertedContent);
        pagination.setTotalElements(getTotalElements());
        pagination.setTotalPages(getTotalPages());
        pagination.setSortPageParam(getSortPageParam());
        return pagination;
    }

    protected <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        if (items == null) {
            return null;
        }
        return items.stream().map(converter::apply).collect(Collectors.toList());
    }
}