package com.uaepay.pub.csc.cases.domainservice.compare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.domain.compare.ErrorDetail;
import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;
import com.uaepay.pub.csc.domainservice.compare.compensate.CompensateService;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.RowDataBuilder;

public class CompensateService_fillCompensateTest extends MockTestBase {

    @Autowired
    CompensateService compensateService;

    @Test
    public void testException() {
        ErrorDetail errorDetail = ErrorDetail.lackTarget(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows());
        String expression = "$result.lack";
        ErrorException exception =
            Assertions.assertThrows(ErrorException.class, () -> checkCompensate(errorDetail, expression));
        Assertions.assertEquals(TaskErrorCodeEnum.COMPENSATE_EXPRESSION_ERROR.getCode(), exception.getCode());
        Assertions.assertEquals(expression, exception.getMessage());
    }

    @Test
    public void testLack() {
        ErrorDetail errorDetail = ErrorDetail.lackTarget(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows());
        String expression = "$result.lack()";
        checkCompensate(errorDetail, expression);
    }

    @Test
    public void testLack_noMatch() {
        ErrorDetail errorDetail = ErrorDetail.lackTarget(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "P").buildGroupRows());
        String expression = "$result.lack('status', 'S')";
        checkNoCompensate(errorDetail, expression);
    }

    @Test
    public void testLack_match() {
        ErrorDetail errorDetail = ErrorDetail.lackTarget(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows());
        String expression = "$result.lack('status', 'S')";
        checkCompensate(errorDetail, expression);
    }

    @Test
    public void testLack_match_notifyStatus() {
        ErrorDetail errorDetail = ErrorDetail.lackTarget(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows());
        String expression = "$result.lack('status', 'S', 'S1')";
        checkCompensate(errorDetail, expression, "S1");
    }

    @Test
    public void testMismatch_noMatch() {
        GroupRows srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows();
        GroupRows targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "Success").buildGroupRows();
        ErrorDetail errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        String expression = "$result.mismatch('status', 'trade_status', 'S', 'P')";
        checkNoCompensate(errorDetail, expression);
    }

    @Test
    public void testMismatch_match() {
        GroupRows srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows();
        GroupRows targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "Process").buildGroupRows();
        ErrorDetail errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        String expression = "$result.mismatch('status', 'trade_status', 'S', 'Process')";
        checkCompensate(errorDetail, expression);
    }

    @Test
    public void testMismatch_match_notifyStatus() {
        GroupRows srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").buildGroupRows();
        GroupRows targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "Process").buildGroupRows();
        ErrorDetail errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        String expression = "$result.mismatch('status', 'trade_status', 'S', 'Process', 'S1')";
        checkCompensate(errorDetail, expression, "S1");
    }

    @Test
    public void testMismatchMiss() {
        GroupRows srcGroup, targetGroup;
        ErrorDetail errorDetail;
        String expression = "$result.mismatchMiss('status', 'trade_status', 'SETTLED', 'PAID')"
            + "$result.mismatchMiss('status', 'trade_status', 'SETTLED', 'SUCCESS')"
            + "$result.mismatchMiss('status', 'trade_status', 'PAID', 'PAID')";

        // 少了 SUCCESS
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkCompensate(errorDetail, expression);

        // 少了 PAID
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkCompensate(errorDetail, expression);

        // 正常
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .row("a", "SUCCESS").buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkNoCompensate(errorDetail, expression);

        // 正常
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "PAID").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkNoCompensate(errorDetail, expression);
    }

    @Test
    public void testMismatchMiss_notifyStatus() {
        GroupRows srcGroup, targetGroup;
        ErrorDetail errorDetail;
        String expression = "$result.mismatchMiss('status', 'trade_status', 'SETTLED', 'PAID', 'PAID')"
            + "$result.mismatchMiss('status', 'trade_status', 'SETTLED', 'SUCCESS', 'SUCCESS')"
            + "$result.mismatchMiss('status', 'trade_status', 'PAID', 'PAID', 'PAID')";

        // 少了 SUCCESS
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkCompensate(errorDetail, expression, "SUCCESS");

        // 少了 PAID
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkCompensate(errorDetail, expression, "PAID");

        // 正常
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "SETTLED").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .row("a", "SUCCESS").buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkNoCompensate(errorDetail, expression);

        // 正常
        srcGroup =
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "PAID").buildGroupRows();
        targetGroup = new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "PAID")
            .buildGroupRows();
        errorDetail = ErrorDetail.mismatch(srcGroup, targetGroup, null);
        checkNoCompensate(errorDetail, expression);
    }

    private void checkNoCompensate(ErrorDetail errorDetail, String expression) {
        boolean result = compensateService.fillCompensateFlag(errorDetail, expression);
        Assertions.assertFalse(result);
        Assertions.assertNull(errorDetail.getCompensateFlag());
        Assertions.assertNull(errorDetail.getNotifyStatus());
    }

    private void checkCompensate(ErrorDetail errorDetail, String expression, String... notifyStatus) {
        boolean result = compensateService.fillCompensateFlag(errorDetail, expression);
        Assertions.assertTrue(result);
        Assertions.assertEquals(CompensateFlagEnum.WAIT, errorDetail.getCompensateFlag());
        if (notifyStatus != null && notifyStatus.length >= 1) {
            Assertions.assertEquals(notifyStatus[0], errorDetail.getNotifyStatus());
        } else {
            Assertions.assertNull(errorDetail.getNotifyStatus());
        }
    }

    // SrcRows srcRows = new SrcRows(
    // new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").row("b", "S").build());
    // TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status")
    // .relate("order_no").row("a", "SUCCESS").row("b", "PAID").build());

}
