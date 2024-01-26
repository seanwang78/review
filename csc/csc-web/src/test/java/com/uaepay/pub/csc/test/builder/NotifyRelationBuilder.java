package com.uaepay.pub.csc.test.builder;

import java.util.*;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyDefineGroup;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroupContact;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;
import com.uaepay.pub.csc.test.domain.NotifyDefine;

public class NotifyRelationBuilder {

    public static final String MEMO = "UNIT-TEST";

    List<NotifyGroup> groups = new ArrayList<>();

    List<NotifyContact> contacts = new ArrayList<>();

    Map<NotifyGroup, List<NotifyContact>> groupContact = new HashMap<>();

    Map<NotifyDefine, List<NotifyGroup>> defineGroup = new HashMap<>();

    public NotifyGroup buildGroup(String groupName) {
        return buildGroup(groupName, YesNoEnum.YES);
    }

    public NotifyGroup buildGroup(String groupName, YesNoEnum enableFlag) {
        NotifyGroup group = new NotifyGroup();
        group.setEnableFlag(enableFlag);
        group.setGroupName(groupName);
        group.setMemo(MEMO);
        groups.add(group);
        return group;
    }

    public NotifyDefine buildDefine(DefineTypeEnum defineType, long defineId) {
        return new NotifyDefine(defineType, defineId);
    }

    public NotifyContactBuilder contactBuilder(String contactName, String email) {
        return new NotifyContactBuilder(contactName, email);
    }

    public NotifyRelationBuilder relate(NotifyGroup group, NotifyContact... contacts) {
        if (!groupContact.containsKey(group)) {
            groupContact.put(group, new ArrayList<>());
        }
        groupContact.get(group).addAll(Arrays.asList(contacts));
        return this;
    }

    public NotifyRelationBuilder relate(NotifyDefine define, NotifyGroup... group) {
        if (!defineGroup.containsKey(define)) {
            defineGroup.put(define, new ArrayList<>());
        }
        defineGroup.get(define).addAll(Arrays.asList(group));
        return this;
    }

    public class NotifyContactBuilder {

        public NotifyContactBuilder(String contactName, String email) {
            contact = new NotifyContact();
            contact.setContactName(contactName);
            contact.setEmail(email);
            contact.setEnableFlag(YesNoEnum.YES);
            contact.setNormalNotifyType(NotifyTypeEnum.EMAIL);
            contact.setUrgentNotifyType(NotifyTypeEnum.SMS);
            contact.setMemo(MEMO);
        }

        NotifyContact contact;

        public NotifyContactBuilder disable() {
            contact.setEnableFlag(YesNoEnum.NO);
            return this;
        }

        public NotifyContactBuilder mobile(String mobile) {
            contact.setMobile(mobile);
            return this;
        }

        public NotifyContact build() {
            contacts.add(contact);
            return contact;
        }
    }

    public List<NotifyGroup> getGroups() {
        return groups;
    }

    public List<NotifyContact> getContacts() {
        return contacts;
    }

    public List<NotifyGroupContact> buildGroupContact() {
        List<NotifyGroupContact> result = new ArrayList<>();
        groupContact.forEach((group, contacts) -> {
            contacts.forEach(contact -> {
                NotifyGroupContact relation = new NotifyGroupContact();
                relation.setGroupId(group.getGroupId());
                relation.setContactId(contact.getContactId());
                result.add(relation);
            });
        });
        return result;
    }

    public List<NotifyDefineGroup> buildDefineGroup() {
        List<NotifyDefineGroup> result = new ArrayList<>();
        defineGroup.forEach((define, groups) -> {
            groups.forEach(group -> {
                NotifyDefineGroup relation = new NotifyDefineGroup();
                relation.setDefineType(define.getDefineType());
                relation.setDefineId(define.getDefineId());
                relation.setGroupId(group.getGroupId());
                result.add(relation);
            });
        });
        return result;
    }
}
