package com.uaepay.gateway.cgs.app.template.misc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/27
 * @since 0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassMetadata {
    class NULL{}

    public static final ClassMetadata NULL_META = new ClassMetadata(NULL.class);

    public ClassMetadata(Class clazz) {
        this.clazz = clazz;
    }

    /** 当前类类型 */
    private Class clazz;

    /** 当前类属性 */
    private List<FieldMetadata> fields;

    /** 父类类描述信息 */
    private ClassMetadata superClass;
}