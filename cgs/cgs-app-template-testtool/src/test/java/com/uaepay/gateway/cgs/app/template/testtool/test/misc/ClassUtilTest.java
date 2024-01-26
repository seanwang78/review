package com.uaepay.gateway.cgs.app.template.testtool.test.misc;

import com.uaepay.gateway.cgs.app.template.misc.ResponseHandler;
import com.uaepay.gateway.cgs.app.template.misc.annotation.Placeholder;
import com.uaepay.gateway.cgs.app.template.misc.model.ClassMetadata;
import com.uaepay.gateway.cgs.app.template.misc.util.ClassUtil;
import lombok.Data;
import org.junit.Test;

import java.util.*;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/4
 * @since 0.1
 */
public class ClassUtilTest {

    @Data
    class Super {
        @Placeholder
        private String name;

        @Placeholder
        private Map<String, String> map;
    }

    @Data
    class Child extends Super {
        @Placeholder
        private List<String> list;

        @Placeholder
        private String age;

        private List<Child> children;

        private Map<String, Child> childMap;
    }

    ResponseHandler responseHandler = new MockResponseHandler();

    @Test
    public void testParseClassMetadata() {
        responseHandler.parseResponseClass(Arrays.asList(Child.class));
    }

    @Test
    public void testReplace() throws IllegalAccessException {
        ClassMetadata classMetadata = ClassUtil.parseClassMetadata(Child.class);

        Child child = new Child();
        child.setName("name");
        child.setAge("age");
        child.setList(Arrays.asList("list1"));
        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        child.setMap(map);

        {
            List<Child> children = new ArrayList<>();
            Child child1 = new Child();
            child1.setName("name");
            children.add(child1);
            child.setChildren(children);
        }

        {
            Map<String, Child> childMap = new HashMap<>();
            Child child1 = new Child();
            child1.setName("name");
            childMap.put("key", child1);
            child.setChildMap(childMap);
        }

        responseHandler.replacePlaceholder(classMetadata, "en", child);

        System.out.println(child);
    }

}
