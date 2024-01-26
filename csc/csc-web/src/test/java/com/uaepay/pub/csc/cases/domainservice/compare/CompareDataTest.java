package com.uaepay.pub.csc.cases.domainservice.compare;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.domain.compare.CompareData;

public class CompareDataTest {

    @Test
    public void testString() {
        Assertions.assertTrue(CompareData.checkValueEquals("abc", "abc"));
        Assertions.assertTrue(CompareData.checkValueEquals("123", 123));
        Assertions.assertTrue(CompareData.checkValueEquals("123", 123L));
        Assertions.assertTrue(CompareData.checkValueEquals("123", new BigInteger("123")));
        Assertions.assertTrue(CompareData.checkValueEquals("123", new BigDecimal("123.00")));

        Assertions.assertFalse(CompareData.checkValueEquals("abc", "ab"));
        Assertions.assertFalse(CompareData.checkValueEquals("abc", 123));
        Assertions.assertFalse(CompareData.checkValueEquals("123", 12));
        Assertions.assertFalse(CompareData.checkValueEquals("123", 12L));
        Assertions.assertFalse(CompareData.checkValueEquals("123", new BigInteger("12")));
        Assertions.assertFalse(CompareData.checkValueEquals("123", new BigDecimal("12.00")));
    }

    @Test
    public void testInt() {
        Assertions.assertTrue(CompareData.checkValueEquals(123, "123"));
        Assertions.assertTrue(CompareData.checkValueEquals(123, 123));
        Assertions.assertTrue(CompareData.checkValueEquals(123, 123L));
        Assertions.assertTrue(CompareData.checkValueEquals(123, new BigInteger("123")));
        Assertions.assertTrue(CompareData.checkValueEquals(123, new BigDecimal("123.00")));

        Assertions.assertFalse(CompareData.checkValueEquals(123, "abc"));
        Assertions.assertFalse(CompareData.checkValueEquals(123, 12));
        Assertions.assertFalse(CompareData.checkValueEquals(123, 12L));
        Assertions.assertFalse(CompareData.checkValueEquals(123, new BigInteger("12")));
        Assertions.assertFalse(CompareData.checkValueEquals(123, new BigDecimal("12.00")));
    }

    @Test
    public void testLong() {
        Assertions.assertTrue(CompareData.checkValueEquals(123L, "123"));
        Assertions.assertTrue(CompareData.checkValueEquals(123L, 123));
        Assertions.assertTrue(CompareData.checkValueEquals(123L, 123L));
        Assertions.assertTrue(CompareData.checkValueEquals(123L, new BigInteger("123")));
        Assertions.assertTrue(CompareData.checkValueEquals(123L, new BigDecimal("123.00")));

        Assertions.assertFalse(CompareData.checkValueEquals(123L, "abc"));
        Assertions.assertFalse(CompareData.checkValueEquals(123L, 12));
        Assertions.assertFalse(CompareData.checkValueEquals(123L, 12L));
        Assertions.assertFalse(CompareData.checkValueEquals(123L, new BigInteger("12")));
        Assertions.assertFalse(CompareData.checkValueEquals(123L, new BigDecimal("12.00")));
    }

    @Test
    public void testBigInteger() {
        Assertions.assertTrue(CompareData.checkValueEquals(new BigInteger("123"), "123"));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigInteger("123"), 123));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigInteger("123"), 123L));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigInteger("123"), new BigInteger("123")));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigInteger("123"), new BigDecimal("123.00")));

        Assertions.assertFalse(CompareData.checkValueEquals(new BigInteger("123"), "abc"));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigInteger("123"), 12));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigInteger("123"), 12L));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigInteger("123"), new BigInteger("12")));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigInteger("123"), new BigDecimal("12.00")));
    }

    @Test
    public void testBigDecimal() {
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), "123"));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), 123));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), 123L));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), new BigInteger("123")));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), new BigDecimal("123.00")));
        Assertions.assertTrue(CompareData.checkValueEquals(new BigDecimal("123.00"), new BigDecimal("123.0000")));

        Assertions.assertFalse(CompareData.checkValueEquals(new BigDecimal("123.00"), "abc"));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigDecimal("123.00"), 12));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigDecimal("123.00"), 12L));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigDecimal("123.00"), new BigInteger("12")));
        Assertions.assertFalse(CompareData.checkValueEquals(new BigDecimal("123.00"), new BigDecimal("123.0001")));
    }

}
