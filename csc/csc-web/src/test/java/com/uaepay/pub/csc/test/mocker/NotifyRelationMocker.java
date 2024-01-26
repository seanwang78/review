package com.uaepay.pub.csc.test.mocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyContactMapper;
import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyDefineGroupMapper;
import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyGroupContactMapper;
import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyGroupMapper;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;

@Service
public class NotifyRelationMocker {

    @Autowired
    NotifyGroupMapper notifyGroupMapper;

    @Autowired
    NotifyContactMapper notifyContactMapper;

    @Autowired
    NotifyGroupContactMapper notifyGroupContactMapper;

    @Autowired
    NotifyDefineGroupMapper notifyDefineGroupMapper;

    public void mock(NotifyRelationBuilder relationBuilder) {
        relationBuilder.getGroups().forEach(group -> {
            notifyGroupMapper.insertSelective(group);
            System.out.printf("mock notify group: %d\n", group.getGroupId());
        });
        relationBuilder.getContacts().forEach(contact -> {
            notifyContactMapper.insertSelective(contact);
            System.out.printf("mock notify contact: %d\n", contact.getContactId());
        });
        relationBuilder.buildGroupContact().forEach(relation -> {
            notifyGroupContactMapper.insertSelective(relation);
            System.out.printf("mock group contact relation: %d -> %d - %d\n", relation.getGroupId(),
                relation.getContactId(), relation.getRelateId());
        });

        relationBuilder.buildDefineGroup().forEach(relation -> {
            notifyDefineGroupMapper.insertSelective(relation);
            System.out.printf("mock define group relation: %d -> %d - %d\n", relation.getDefineId(),
                relation.getGroupId(), relation.getRelateId());
        });
    }

}
