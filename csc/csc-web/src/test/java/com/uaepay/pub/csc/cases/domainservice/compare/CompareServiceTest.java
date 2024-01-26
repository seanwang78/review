package com.uaepay.pub.csc.cases.domainservice.compare;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domainservice.compare.check.CompareService;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.RowDataBuilder;

public class CompareServiceTest extends MockTestBase {

    @Autowired
    CompareService compareService;

    @Test
    public void testCorrespond_success() {
        SrcRows srcRows = new SrcRows(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").row("b", "S").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status")
            .relate("order_no").row("a", "SUCCESS").row("b", "PAID").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'SUCCESS,PAID')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 2, 0, 0);
    }

    @Test
    public void testCorrespond_srcFieldNotExist() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS").build());
        String checkExpression = "$data.isCorrespond('status1', 'trade_status', 'S', 'SUCCESS,PAID')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("source field [status1] not exist",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testCorrespond_targetFieldNotExist() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status1', 'S', 'SUCCESS,PAID')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("target field [trade_status1] not exist",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testCorrespond_fail() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'FAIL')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("status[S] and trade_status[SUCCESS] not correspond, rule: [S] -> [FAIL]",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testCorrespond_skip() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'F', 'FAIL')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testCorrespondAll_fail() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "SUCCESS").build());
        String checkExpression = "$data.isCorrespondAll('status', 'trade_status', 'S', 'SUCCESS,PAID')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals(
            "status[S] and trade_status[remain [PAID]] not correspond all, rule: [S] -> [SUCCESS,PAID]",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testCorrespondAll_success() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status")
            .relate("order_no").row("a", "SUCCESS").row("a", "PAID").build());
        String checkExpression = "$data.isCorrespondAll('status', 'trade_status', 'S', 'SUCCESS,PAID')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testCorrespondAll_skip() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status")
            .relate("order_no").row("a", "SUCCESS").row("a", "PAID").build());
        String checkExpression = "$data.isCorrespondAll('status', 'trade_status', 'F', 'FAIL')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testEqual_success() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        String checkExpression = "$data.isEqual('status', 'trade_status')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testEqual_success_bigDecimalScale() {
        SrcRows srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "amount").relate("order_no")
            .row("a", new BigDecimal("0.10")).build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "amount").relate("order_no")
            .row("a", new BigDecimal("0.1000")).build());
        String checkExpression = "$data.isEqual('amount', 'amount')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testEqual_srcFieldNotExist() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        String checkExpression = "$data.isEqual('status1', 'trade_status')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("source field [status1] not exist",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testEqual_targetFieldNotExist() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        String checkExpression = "$data.isEqual('status', 'trade_status1')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("target field [trade_status1] not exist",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testEqual_fail() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "F").build());
        String checkExpression = "$data.isEqual('status', 'trade_status')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("status[S] and trade_status[F] not equal",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testIn_success() {
        SrcRows srcRows = new SrcRows(
            new RowDataBuilder().columns("order_no", "product_code").relate("order_no").row("a", "1").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "F").build());
        String checkExpression = "$data.isIn('product_code', '1,2')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);
    }

    @Test
    public void testIn_srcFieldNotExist() {
        SrcRows srcRows = new SrcRows(
            new RowDataBuilder().columns("order_no", "product_code").relate("order_no").row("a", "3").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "F").build());
        String checkExpression = "$data.isIn('product_code2', '1,2')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("source field [product_code2] not exist",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testIn_fail() {
        SrcRows srcRows = new SrcRows(
                new RowDataBuilder().columns("order_no", "product_code").relate("order_no").row("a", "3").build());
        TargetRows targetRows = new TargetRows(
                new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "F").build());
        String checkExpression = "$data.isIn('product_code', '1,2')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("product_code[3] not in list[1,2]",
                result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testLack_target() {
        SrcRows srcRows =
            new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S").build());
        TargetRows targetRows =
            new TargetRows(new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").build());
        String checkExpression = "$data.isEqual('status', 'trade_status')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 1, 0);
        Assertions.assertEquals(1, result.getLackDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("lack target data", result.getLackDetails().get(0).getErrorMessage());
    }

    @Test
    public void testLack_src() {
        SrcRows srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no").build());
        TargetRows targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        String checkExpression = "$data.isEqual('status', 'trade_status')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 1, 0);
        Assertions.assertEquals(1, result.getLackDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("lack source data", result.getLackDetails().get(0).getErrorMessage());
    }

    /**
     * 一条源数据对应多条目标数据的核对场景
     */
    @Test
    public void test_1_to_n() {
        SrcRows srcRows;
        TargetRows targetRows;
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'PAID,SUCCESS')";
        CompareResult result;

        // success
        srcRows = new SrcRows(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S", "AED").build());
        targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "PAID").row("a", "SUCCESS").build());
        result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);

        // fail
        srcRows = new SrcRows(
            new RowDataBuilder().columns("order_no", "status").relate("order_no").row("a", "S", "AED").build());
        targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "PAID").row("a", "FAIL").build());
        result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("status[S] and trade_status[FAIL] not correspond, rule: [S] -> [PAID,SUCCESS]",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void test_n_to_1() {
        SrcRows srcRows;
        TargetRows targetRows;
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'PAID,SUCCESS', 'S')"
            + "$data.isCorrespond('status', 'trade_status', 'FAIL', 'F')";
        CompareResult result;

        // success
        srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no")
            .row("a", "PAID", "AED").row("a", "SUCCESS", "AED").build());
        targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 0, 0);

        // fail
        srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no")
            .row("a", "PAID", "AED").row("a", "FAIL", "AED").build());
        targetRows = new TargetRows(
            new RowDataBuilder().columns("order_no", "trade_status").relate("order_no").row("a", "S").build());
        result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(1, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("status[FAIL] and trade_status[S] not correspond, rule: [FAIL] -> [F]",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void test_n_to_n() {
        SrcRows srcRows;
        TargetRows targetRows;
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'PAID,SUCCESS', 'S')"
            + "$data.isCorrespond('status', 'trade_status', 'FAIL', 'F')";
        CompareResult result;

        srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status").relate("order_no")
            .row("a", "PAID", "AED").row("a", "SUCCESS", "AED").build());
        targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status").relate("order_no")
            .row("a", "S").row("a", "F").build());
        result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 0, 0, 1);
        Assertions.assertEquals(2, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals(
            "status[PAID] and trade_status[F] not correspond, rule: [PAID,SUCCESS] -> [S]"
                + ", status[SUCCESS] and trade_status[F] not correspond, rule: [PAID,SUCCESS] -> [S]",
            result.getMismatchDetails().get(0).getErrorMessage());
    }

    @Test
    public void testMix_success() {
        SrcRows srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status", "currency").relate("order_no")
            .row("a", "S", "AED").row("b", "F", "AED").row("c", "F", "AED").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status", "currency")
            .relate("order_no").row("a", "Success", "AED").row("b", "Fail", "AED").row("c", "Failure", "AED").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'Success')\n"
            + "$data.isCorrespond('status', 'trade_status', 'F', 'Fail,Failure')\n"
            + "$data.isEqual('currency', 'currency')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 3, 0, 0);
    }

    @Test
    public void testMix_fail() {
        SrcRows srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status", "currency").relate("order_no")
            .row("a", "S", "AED").row("b", "F", "AED").row("c", "F", "AED").row("d", "S", "AED").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status", "currency")
            .relate("order_no").row("a", "Fail", "CNY").row("b", "Success", "AED").row("c", "Failure", "AED")
            .row("e", "F", "AED").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'Success')\n"
            + "$data.isCorrespond('status', 'trade_status', 'F', 'Fail,Failure')\n"
            + "$data.isEqual('currency', 'currency')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        checkCompareResult(result, 1, 2, 2);

        Assertions.assertEquals(2, result.getMismatchDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("status[S] and trade_status[Fail] not correspond, rule: [S] -> [Success]",
            result.getMismatchDetails().get(0).getErrorMessages().get(0));
        Assertions.assertEquals("currency[AED] and currency[CNY] not equal",
            result.getMismatchDetails().get(0).getErrorMessages().get(1));

        Assertions.assertEquals(1, result.getMismatchDetails().get(1).getErrorMessages().size());
        Assertions.assertEquals("status[F] and trade_status[Success] not correspond, rule: [F] -> [Fail,Failure]",
            result.getMismatchDetails().get(1).getErrorMessages().get(0));

        Assertions.assertEquals(1, result.getLackDetails().get(0).getErrorMessages().size());
        Assertions.assertEquals("lack target data", result.getLackDetails().get(0).getErrorMessage());

        Assertions.assertEquals(1, result.getLackDetails().get(1).getErrorMessages().size());
        Assertions.assertEquals("lack source data", result.getLackDetails().get(1).getErrorMessage());
    }

    private void checkCompareResult(CompareResult result, int expectPassCount, int expectLackCount,
        int expectMismatchCount) {
        Assertions.assertEquals(expectPassCount, result.getPassCount());
        Assertions.assertEquals(expectLackCount, result.getLackDetails().size());
        Assertions.assertEquals(expectMismatchCount, result.getMismatchDetails().size());
    }

}
