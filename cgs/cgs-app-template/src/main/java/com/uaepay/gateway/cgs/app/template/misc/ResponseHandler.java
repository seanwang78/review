package com.uaepay.gateway.cgs.app.template.misc;

import com.alibaba.fastjson.JSON;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.template.misc.annotation.Placeholder;
import com.uaepay.gateway.cgs.app.template.misc.model.ClassMetadata;
import com.uaepay.gateway.cgs.app.template.misc.model.FieldMetadata;
import com.uaepay.gateway.common.app.template.domainservice.language.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.uaepay.gateway.cgs.app.template.misc.util.ClassUtil.isCollectionType;
import static com.uaepay.gateway.cgs.app.template.misc.util.ClassUtil.isMapType;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/2/29
 * @since 0.1
 */
@Slf4j
@Component
public class ResponseHandler{

    private Map<Class, ClassMetadata> i18nContainer = new ConcurrentHashMap<>();
    private static final Predicate<Annotation> I18N_PREDICATE = annotation -> annotation instanceof Placeholder;
    @Autowired
    private LanguageService languageService;

    public void parseResponseClass(List<Type> responseTypes) {
        if (CollectionUtils.isEmpty(responseTypes)) {
            return;
        }

//        for (Type responseType : responseTypes) {
//            if (responseType instanceof Class && !isVoid((Class) responseType)) {
//                Class clazz = (Class) responseType;
//                ClassMetadata classMetadata = parseClassMetadata(clazz);
//                if (filterClassMetadataWithAnnotation(classMetadata, I18N_PREDICATE, true)) {
//                    i18nContainer.put(clazz, classMetadata);
//                }
//            }
//        }
    }

    boolean filterClassMetadataWithAnnotation(ClassMetadata metadata, Predicate<Annotation> predicate) {
        return filterClassMetadataWithAnnotation(metadata, predicate, false);
    }

    /**
     * 根据Annotation过滤
     * @author Zhibin Liu
     * @time 2020/2/29 15:22
     * @param
     */
    boolean filterClassMetadataWithAnnotation(ClassMetadata metadata, Predicate<Annotation> predicate, boolean removeNoMatch) {
        if (metadata.getFields() == null) {
            return false;
        }

        boolean classMatch = false, fieldMatch = false;

        // 1、定义是否匹配到Annotation
        if (metadata.getSuperClass() != null) {
            classMatch |= filterClassMetadataWithAnnotation(metadata.getSuperClass(), predicate, removeNoMatch);
        }
        Iterator<FieldMetadata> iterator = metadata.getFields().iterator();
        Class parentClazz = metadata.getClazz();
        while (iterator.hasNext()) {
            boolean matchAnnotation = false;
            FieldMetadata fm = iterator.next();
            if (isSecurityDeduce(parentClazz, fm.getTypeMeta())) {
                matchAnnotation = filterClassMetadataWithAnnotation(fm.getTypeMeta(), predicate, removeNoMatch);
            }
            if (!isEmpty(fm.getFieldGenericTypes())) {
                for (ClassMetadata cm : fm.getFieldGenericTypes()) {
                    if (isSecurityDeduce(parentClazz, cm)) {
                        matchAnnotation |= filterClassMetadataWithAnnotation(cm, predicate, removeNoMatch);
                    }
                }
            }
            if (fm.getAnnotations() != null) {
                for (Annotation annotation : fm.getAnnotations()) {
                    matchAnnotation |= predicate.test(annotation);
                }
            }
            if (!matchAnnotation && removeNoMatch) {
                iterator.remove();
            }
            fieldMatch |= matchAnnotation;
        }
        return classMatch || fieldMatch;
    }


    public void replacePlaceholder(ClassMetadata classMetadata, String langType, Object object) throws IllegalAccessException {
        if (classMetadata.getSuperClass() != null) {
            replacePlaceholder(classMetadata.getSuperClass(), langType, object);
        }
        List<FieldMetadata> fields = classMetadata.getFields();
        if (fields == null) {
            return;
        }
        for (FieldMetadata fieldMetadata : fields) {
            Field field = fieldMetadata.getField();
            ClassMetadata typeMeta = fieldMetadata.getTypeMeta();
            field.setAccessible(true);

            // 判断当前属性上是否有Placeholder的注解
            if (existsPlaceholderAnnotation(fieldMetadata)) {
                resolveValue(field, langType, field.get(object), object);
            }
            // 当前属性上没有 继续向下递归
            else if (typeMeta != null) {
                Object fieldReturnValue = field.get(object);
                if (fieldReturnValue != null) {

                    // 判断属性是否是集合类型
                    if (isCollectionType(typeMeta.getClazz())
                            && !isEmpty(fieldMetadata.getFieldGenericTypes())) {
                        ClassMetadata genericTypeMeta = fieldMetadata.getFieldGenericTypes().get(0);
                        Collection collection = (Collection) fieldReturnValue;
                        Iterator iter = collection.iterator();
                        while (iter.hasNext()) {
                            Object next = iter.next();
                            replacePlaceholder(genericTypeMeta, langType, next);
                        }
                    }
                    // 判断属性是否是Map类型
                    else if (isMapType(typeMeta.getClazz())
                            && !isEmpty(fieldMetadata.getFieldGenericTypes())) {
                        // ClassMetadata keyType = fieldMetadata.getFieldGenericTypes().get(0);
                        ClassMetadata valueType = fieldMetadata.getFieldGenericTypes().get(1);

                        Map map = (Map) fieldReturnValue;
                        Iterator iter = map.values().iterator();
                        while (iter.hasNext()) {
                            Object next = iter.next();
                            replacePlaceholder(valueType, langType, next);
                        }
                    }
                    else {
                        replacePlaceholder(typeMeta, langType, fieldReturnValue);
                    }
                }
            }
        }
    }

    /**
     * 解析Placehondler注解的值
     * @author Zhibin Liu
     * @time 2020/3/4 10:54
     * @param field 有注解的属性
     * @param langType 国际化语言
     * @param fieldVal 属性值
     * @param bean 当前属性所在类的对象
     */
    private void resolveValue(Field field, String langType, Object fieldVal, Object bean) throws IllegalAccessException {
        if (fieldVal != null) {
            // 替换String
            if (fieldVal instanceof String) {
                String placeholder = resolve(langType, (String) fieldVal);
                field.set(bean, placeholder);
            }
            // 替换List中的元素
            else if (fieldVal instanceof List) {
                List list = (List) fieldVal;
                for (int i = 0; i < list.size(); i++) {
                    Object value = list.get(i);
                    if (value instanceof String) {
                        String placeholder = resolve(langType, (String) value);
                        list.set(i, placeholder);
                    }
                }
            }
            // 替换Map中values的元素
            else if (fieldVal instanceof Map) {
                Map map = (Map) fieldVal;
                for (Object o : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        String placeholder = resolve(langType, (String) value);
                        entry.setValue(placeholder);
                    }
                }
            }
        }
    }

    /**
     * 判断属性是否存在I18N注解
     * @author Zhibin Liu
     * @time 2020/2/29 15:35
     * @param fieldMetadata
     */
    private boolean existsPlaceholderAnnotation(FieldMetadata fieldMetadata) {
        if (fieldMetadata.getAnnotations() != null) {
            for (Annotation annotation : fieldMetadata.getAnnotations()) {
                if (I18N_PREDICATE.test(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否安全推断
     *
     * @return true 是安全的
     * @return false 不是安全的
     */
    private boolean isSecurityDeduce(Class parentClazz, ClassMetadata classMeta) {
        return classMeta != null
                && parentClazz != null
                && !parentClazz.equals(classMeta.getClazz());
    }

    /**
     * 处理响应
     * @return
     */
    public ClientServiceResponse handle(ClientGatewayRequest request, ClientServiceResponse response) {
        try {
            // 1.检查是否有i18n国际化需求
            if (response.getBody() != null) {
                ClassMetadata classMetadata = i18nContainer.get(response.getBody().getClass());
                if (classMetadata != null) {
                    replacePlaceholder(classMetadata, request.getLangType(), response.getBody());
                }
            }
        } catch (Exception e) {
            log.error("[ResponseHandler -> handle -> fail, response: {} ]", JSON.toJSONString(response), e);
        }
        return response;
    }


    public String resolve(String langType, String message, Object... params) {
        return languageService.resolve(langType, message, params);
    }
}
