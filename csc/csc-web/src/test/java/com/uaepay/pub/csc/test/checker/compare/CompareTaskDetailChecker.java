package com.uaepay.pub.csc.test.checker.compare;

import org.junit.jupiter.api.Assertions;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.service.facade.enums.CompareStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;

public class CompareTaskDetailChecker {

    public CompareTaskDetailChecker(CompareDetail detail) {
        this.detail = detail;
    }

    private CompareDetail detail;

    public CompareTaskDetailChecker identity(String relateIdentity) {
        Assertions.assertEquals(relateIdentity, detail.getRelateIdentity());
        return this;
    }

    public CompareTaskDetailChecker lackTarget(String srcData) {
        Assertions.assertEquals(CompareStatusEnum.LACK, detail.getCompareStatus());
        Assertions.assertEquals(srcData, detail.getSrcData());
        Assertions.assertNull(detail.getTargetData());
        Assertions.assertEquals("lack target data", detail.getErrorMessage());
        Assertions.assertNull(detail.getCompensateFlag());
        return this;
    }

    public CompareTaskDetailChecker lackTargetCompensate(String srcData, CompensateFlagEnum compensateFlag,
                                                         String... notifyStatus) {
        Assertions.assertEquals(CompareStatusEnum.LACK, detail.getCompareStatus());
        Assertions.assertEquals(srcData, detail.getSrcData());
        Assertions.assertNull(detail.getTargetData());
        Assertions.assertEquals("lack target data", detail.getErrorMessage());
        Assertions.assertEquals(compensateFlag, detail.getCompensateFlag());
        if (notifyStatus != null && notifyStatus.length >= 1) {
            Assertions.assertEquals(notifyStatus[0], detail.getExtension(CompareDetail.NOTIFY_STATUS));
        } else {
            Assertions.assertNull(detail.getExtension());
        }
        return this;
    }

    public CompareTaskDetailChecker lackSrc(String targetData) {
        Assertions.assertEquals(CompareStatusEnum.LACK, detail.getCompareStatus());
        Assertions.assertNull(detail.getSrcData());
        Assertions.assertEquals(targetData, detail.getTargetData());
        Assertions.assertEquals("lack source data", detail.getErrorMessage());
        Assertions.assertNull(detail.getCompensateFlag());
        return this;
    }

    public CompareTaskDetailChecker mismatch(String srcData, String targetData, String errorMessage) {
        Assertions.assertEquals(CompareStatusEnum.MISMATCH, detail.getCompareStatus());
        Assertions.assertEquals(srcData, detail.getSrcData());
        Assertions.assertEquals(targetData, detail.getTargetData());
        Assertions.assertEquals(errorMessage, detail.getErrorMessage());
        Assertions.assertNull(detail.getCompensateFlag());
        return this;
    }

    public CompareTaskDetailChecker mismatchCompensate(String srcData, String targetData, String errorMessage,
                                                       CompensateFlagEnum compensateFlag, String... notifyStatus) {
        Assertions.assertEquals(CompareStatusEnum.MISMATCH, detail.getCompareStatus());
        Assertions.assertEquals(srcData, detail.getSrcData());
        Assertions.assertEquals(targetData, detail.getTargetData());
        Assertions.assertEquals(errorMessage, detail.getErrorMessage());
        Assertions.assertEquals(compensateFlag, detail.getCompensateFlag());
        if (notifyStatus != null && notifyStatus.length >= 1) {
            Assertions.assertEquals(notifyStatus[0], detail.getExtension(CompareDetail.NOTIFY_STATUS));
        } else {
            Assertions.assertNull(detail.getExtension());
        }
        return this;
    }

    public CompareTaskDetailChecker compensateFlag(CompensateFlagEnum compensateFlag) {
        Assertions.assertEquals(compensateFlag, detail.getCompensateFlag());
        return this;
    }

}
