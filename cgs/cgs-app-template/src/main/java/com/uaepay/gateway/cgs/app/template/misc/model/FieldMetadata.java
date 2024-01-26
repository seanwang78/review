package com.uaepay.gateway.cgs.app.template.misc.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/27
 * @since 0.1
 */
@Data
public class FieldMetadata {

    /** 属性字段 */
    private Field field;

    /** 属性类型描述 */
    private ClassMetadata typeMeta;

    /** 属性的范型类型 */
    private List<ClassMetadata> fieldGenericTypes;

    /** 属性注解 */
    private Annotation[] annotations;

    @Override
    public String toString() {
        return "FieldMetadata{" +
                "field=" + field +
                ", typeMeta=" + typeMeta +
                ", fieldGenericTypes=" + fieldGenericTypes +
                ", annotations=" + Arrays.toString(annotations) +
                '}';
    }
}