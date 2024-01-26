package com.uaepay.gateway.cgs.app.template.misc.util;

import com.uaepay.gateway.cgs.app.template.misc.model.ClassMetadata;
import com.uaepay.gateway.cgs.app.template.misc.model.FieldMetadata;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.uaepay.gateway.cgs.app.template.constants.BasisType.getPrimitiveMetadata;
import static com.uaepay.gateway.cgs.app.template.constants.BasisType.getSimpleTypeSet;
import static com.uaepay.gateway.cgs.app.template.misc.model.ClassMetadata.NULL_META;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/29
 * @since 0.1
 */
@Slf4j
public class ClassUtil {

    /**
     * 是否Map类型
     */
    public static boolean isMapType(Class type) {
        return Map.class.isAssignableFrom(type);
    }
    
    /**
     * 是否简单类型
     */
    public static boolean isSimpleType(Class clazz) {
        return getSimpleTypeSet().contains(clazz);
    }
    
    /**
     * 是否集合类型
     */
    public static boolean isCollectionType(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 是Void返回值
     */
    public static boolean isVoid(Class clazz) {
        return void.class.equals(clazz) || Void.class.equals(clazz);
    }


    /**
     * 解析对象
     * @author Zhibin Liu
     * @time 2020/2/29 15:22
     * @param clazz
     */
    public static ClassMetadata parseClassMetadata(Class clazz) {
        try {

            if (clazz == null) {
                return NULL_META;
            }
            if (isSimpleType(clazz)) {
                return getPrimitiveMetadata().get(clazz);
            }

            // 1、构造类描述信息对象
            ClassMetadata classMetadata = new ClassMetadata();
            classMetadata.setClazz(clazz);

            // 2、构造属性集合
            List<FieldMetadata> fieldMetadata = new ArrayList<>();
            classMetadata.setFields(fieldMetadata);

            // 3、解析类的父级属性
            ClassMetadata superClass = parseClassMetadata(clazz.getSuperclass());
            classMetadata.setSuperClass(superClass);

            // 4、解析属性字段
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                f.setAccessible(true);
                if (!isAvailableFields(f)) {
                    continue;
                }
                FieldMetadata fieldMeta = new FieldMetadata();
                fieldMeta.setField(f);

                // 处理Annotation
                Annotation[] annotations = f.getAnnotations();
                if (annotations != null && annotations.length > 0) {
                    fieldMeta.setAnnotations(annotations);
                }

                // 处理属性的类型 如果类的属性是自己 会发生死递归 判断处理一下
                if (!f.getType().equals(clazz)) {
                    fieldMeta.setTypeMeta(parseClassMetadata(f.getType()));
                } else {
                    fieldMeta.setTypeMeta(classMetadata);
                }


                // 处理属性的范型
                {
                    // 处理集合类型
                    if (isCollectionType(f.getType())) {
                        Type[] types = getGenericTypes(f);
                        if (types != null && types.length > 0) {
                            if (types[0] instanceof Class) {
                                Class genericType = (Class) types[0];
                                if (!genericType.equals(clazz)) {
                                    fieldMeta.setFieldGenericTypes(Arrays.asList(parseClassMetadata(genericType)));
                                } else {
                                    fieldMeta.setFieldGenericTypes(Arrays.asList(classMetadata));
                                }
                            }
                        }
                    }
                    // 处理Map
                    if (isMapType(f.getType())) {
                        Type[] types = getGenericTypes(f);
                        if (types != null && types.length > 0) {
                            Class keyClass = types[0] instanceof Class ? (Class) types[0] : null,
                                    valueClass = types[1] instanceof Class ? (Class) types[1] : null;
                            ClassMetadata keyMeta = keyClass.equals(clazz) ? classMetadata : parseClassMetadata(keyClass),
                                    valueMeta = valueClass.equals(clazz) ? classMetadata : parseClassMetadata(valueClass);
                            List<ClassMetadata> metas = Arrays.asList(keyMeta, valueMeta);
                            fieldMeta.setFieldGenericTypes(metas);
                        }
                    }
                }

                fieldMetadata.add(fieldMeta);
            }
            return classMetadata;
        } catch (Exception e) {
            log.error("ClassUtil解析类错误，class = {}", clazz);
            throw e;
        }
    }

    /**
     * 获取属性的范型
     * @author Zhibin Liu
     * @time 2020/2/29 16:19
     * @param f
     */
    private static Type[] getGenericTypes(Field f) {
        ParameterizedType genericType = (ParameterizedType) f.getGenericType();
        return genericType.getActualTypeArguments();
    }


    /**
     * 判断是否是有效属性
     * @author Zhibin Liu
     * @time 2020/2/29 14:33
     * @param field 属性
     */
    private static boolean isAvailableFields(Field field) {
        String name = field.getName();
        // 内部类的this指针
        if (name.contains("this$")) {
            return false;
        }
        return true;
    }

}
