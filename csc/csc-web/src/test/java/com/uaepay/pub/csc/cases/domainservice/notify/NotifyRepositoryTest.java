package com.uaepay.pub.csc.cases.domainservice.notify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domainservice.notify.NotifyRepository;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;
import com.uaepay.pub.csc.test.domain.NotifyDefine;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.NotifyRelationMocker;

public class NotifyRepositoryTest extends MockTestBase {

    @Autowired
    NotifyRelationMocker notifyRelationMocker;

    @Autowired
    MonitorMocker monitorMocker;

    @Autowired
    NotifyRepository notifyRepository;

    private static final String PARAM_DEFINE = "define";
    private static final String PARAM_TASK = "task";
    private static final String PARAM_RESULT = "result";
    private static final String PARAM_ENV = "env";
    private static final String PARAM_FORMAT_UTIL = "format";

    static final String CONTACT1_EMAIL = "1@xxx.com";
    static final String CONTACT2_EMAIL = "2@xxx.com";
    static final String CONTACT3_EMAIL = "3@xxx.com";
    static final String CONTACT1_MOBILE = "1";
    static final String CONTACT2_MOBILE = "2";

    long defineMonitor;

    @BeforeAll
    public void setUp() {
        defineMonitor = mockDefine(DefineTypeEnum.MONITOR);
    }

    private long mockDefine(DefineTypeEnum defineType) {
        NotifyRelationBuilder builder = new NotifyRelationBuilder();
        NotifyGroup group1 = builder.buildGroup("group1");
        NotifyGroup group2 = builder.buildGroup("group2");
        NotifyContact contact1 = builder.contactBuilder("contact1", CONTACT1_EMAIL).mobile(CONTACT1_MOBILE).build();
        NotifyContact contact2 = builder.contactBuilder("contact2", CONTACT2_EMAIL).mobile(CONTACT2_MOBILE).build();
        NotifyContact contact3 = builder.contactBuilder("contact3", CONTACT3_EMAIL).build();

        long defineId = monitorMocker.mock(new MonitorDefineBuilder("mock_define").report("whatever"));
        NotifyDefine define1 = builder.buildDefine(defineType, defineId);

        builder.relate(group1, contact1, contact2).relate(group2, contact1, contact3);
        builder.relate(define1, group1, group2);
        notifyRelationMocker.mock(builder);
        return defineId;
    }

    @Test
    public void test_normal() {
        Map<NotifyTypeEnum, List<String>> notifyGroup;
        List<String> contacts;

        notifyGroup = notifyRepository.loadNotifyGroup(NotifyTemplateTypeEnum.REPORT, defineMonitor, null);
        System.out.println(StringUtils.join(notifyGroup, "\n"));
        Assertions.assertEquals(1, notifyGroup.size());
        contacts = notifyGroup.get(NotifyTypeEnum.EMAIL);
        Assertions.assertEquals(3, contacts.size());
        Assertions.assertEquals(CONTACT1_EMAIL, contacts.get(0));
        Assertions.assertEquals(CONTACT2_EMAIL, contacts.get(1));
        Assertions.assertEquals(CONTACT3_EMAIL, contacts.get(2));

        notifyGroup =
            notifyRepository.loadNotifyGroup(NotifyTemplateTypeEnum.ALARM, defineMonitor, AlarmLevelEnum.NORMAL);
        System.out.println(StringUtils.join(notifyGroup, "\n"));
        Assertions.assertEquals(1, notifyGroup.size());
        contacts = notifyGroup.get(NotifyTypeEnum.EMAIL);
        Assertions.assertEquals(3, contacts.size());
        Assertions.assertEquals(CONTACT1_EMAIL, contacts.get(0));
        Assertions.assertEquals(CONTACT2_EMAIL, contacts.get(1));
        Assertions.assertEquals(CONTACT3_EMAIL, contacts.get(2));

        notifyGroup =
            notifyRepository.loadNotifyGroup(NotifyTemplateTypeEnum.ALARM, defineMonitor, AlarmLevelEnum.URGENT);
        System.out.println(StringUtils.join(notifyGroup, "\n"));
        Assertions.assertEquals(2, notifyGroup.size());
        contacts = notifyGroup.get(NotifyTypeEnum.EMAIL);
        Assertions.assertEquals(3, contacts.size());
        Assertions.assertEquals(CONTACT1_EMAIL, contacts.get(0));
        Assertions.assertEquals(CONTACT2_EMAIL, contacts.get(1));
        Assertions.assertEquals(CONTACT3_EMAIL, contacts.get(2));
        contacts = notifyGroup.get(NotifyTypeEnum.SMS);
        Assertions.assertEquals(2, contacts.size());
        Assertions.assertEquals(CONTACT1_MOBILE, contacts.get(0));
        Assertions.assertEquals(CONTACT2_MOBILE, contacts.get(1));
    }

    // @Test
    // public void test_disable() {
    //
    // List<NotifyContact> contacts = notifyRepository.loadNotifyContacts(DefineTypeEnum.MONITOR, defineId);
    // System.out.println(StringUtils.join(contacts, "\n"));
    //
    // Assertions.assertEquals(1, contacts.size());
    // Assertions.assertEquals(contact2.getContactId(), contacts.get(0).getContactId());
    // }

//     @Test
//     public void test_loadNotifyGroup() {
//         Map<NotifyTypeEnum, List<String>> contactGroup =
//                 notifyRepository.loadNotifyGroup(NotifyTemplateTypeEnum.COMPARE, 1, AlarmLevelEnum.IGNORE);
//         contactGroup.toString();
//     }
}
