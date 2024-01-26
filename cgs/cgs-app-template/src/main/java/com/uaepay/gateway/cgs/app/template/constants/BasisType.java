package com.uaepay.gateway.cgs.app.template.constants;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.gateway.cgs.app.template.misc.model.ClassMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/29
 * @since 0.1
 */
public class BasisType {
    private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet<>();
    private static final Map<Class, ClassMetadata> PRIMITIVE_METADATA = new HashMap<>();

    public static Map<Class, ClassMetadata> getPrimitiveMetadata() {
        return Collections.unmodifiableMap(PRIMITIVE_METADATA);
    }

    public static Set<Class<?>> getSimpleTypeSet() {
        return Collections.unmodifiableSet(SIMPLE_TYPE_SET);
    }

    static {
        SIMPLE_TYPE_SET.add(String.class);
        PRIMITIVE_METADATA.put(String.class, new ClassMetadata(String.class));

        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(byte.class);
        PRIMITIVE_METADATA.put(Byte.class, new ClassMetadata(Byte.class));
        PRIMITIVE_METADATA.put(byte.class, new ClassMetadata(Byte.class));

        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(short.class);
        PRIMITIVE_METADATA.put(Short.class, new ClassMetadata(Short.class));
        PRIMITIVE_METADATA.put(short.class, new ClassMetadata(short.class));

        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(char.class);
        PRIMITIVE_METADATA.put(Character.class, new ClassMetadata(Character.class));
        PRIMITIVE_METADATA.put(char.class, new ClassMetadata(char.class));

        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(int.class);
        PRIMITIVE_METADATA.put(Integer.class, new ClassMetadata(Integer.class));
        PRIMITIVE_METADATA.put(int.class, new ClassMetadata(int.class));

        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(long.class);
        PRIMITIVE_METADATA.put(Long.class, new ClassMetadata(Long.class));
        PRIMITIVE_METADATA.put(long.class, new ClassMetadata(long.class));

        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(float.class);
        PRIMITIVE_METADATA.put(Float.class, new ClassMetadata(Float.class));
        PRIMITIVE_METADATA.put(float.class, new ClassMetadata(float.class));

        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(double.class);
        PRIMITIVE_METADATA.put(Double.class, new ClassMetadata(Double.class));
        PRIMITIVE_METADATA.put(double.class, new ClassMetadata(double.class));

        SIMPLE_TYPE_SET.add(Boolean.class);
        SIMPLE_TYPE_SET.add(boolean.class);
        PRIMITIVE_METADATA.put(Boolean.class, new ClassMetadata(Boolean.class));
        PRIMITIVE_METADATA.put(boolean.class, new ClassMetadata(boolean.class));

        SIMPLE_TYPE_SET.add(Date.class);
        PRIMITIVE_METADATA.put(Date.class, new ClassMetadata(Date.class));

        SIMPLE_TYPE_SET.add(Class.class);
        PRIMITIVE_METADATA.put(Class.class, new ClassMetadata(Class.class));

        SIMPLE_TYPE_SET.add(BigInteger.class);
        PRIMITIVE_METADATA.put(BigInteger.class, new ClassMetadata(BigInteger.class));
        SIMPLE_TYPE_SET.add(BigDecimal.class);
        PRIMITIVE_METADATA.put(BigDecimal.class, new ClassMetadata(BigDecimal.class));

        SIMPLE_TYPE_SET.add(Object.class);
        PRIMITIVE_METADATA.put(Object.class, new ClassMetadata(Object.class));

        SIMPLE_TYPE_SET.add(JSONObject.class);
        PRIMITIVE_METADATA.put(JSONObject.class, new ClassMetadata(JSONObject.class));
    }

}
