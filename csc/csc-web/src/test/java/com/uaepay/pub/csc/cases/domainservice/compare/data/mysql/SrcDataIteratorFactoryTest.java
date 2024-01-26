package com.uaepay.pub.csc.cases.domainservice.compare.data.mysql;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.pub.csc.test.base.MockTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIteratorFactory;

public class SrcDataIteratorFactoryTest extends MockTestBase {

    @Autowired
    private SrcDataIteratorFactory srcDataIteratorFactory;

    @Test
    public void testNotExist() {
        FailException e = Assertions.assertThrows(FailException.class, () -> {
            srcDataIteratorFactory.create("whatever", null, 60, null, null);
        });
        Assertions.assertEquals("datasource not exist: whatever", e.getMessage());
    }

    @Test
    public void testConfigError_noAuth() {
        Assertions.assertThrows(CannotGetJdbcConnectionException.class, () -> {
            srcDataIteratorFactory.create("unittest_error_noauth", null, 60, null, null);
        });
    }

    @Test
    public void testConfigError_noDb() {
        Assertions.assertThrows(CannotGetJdbcConnectionException.class, () -> {
            srcDataIteratorFactory.create("unittest_error_nodb", null, 60, null, null);
        });
    }
    //
    // @Test
    // public void testSuccess() throws Exception {
    // JdbcTemplate template = jdbcTemplateFactory.getOrCreate("default");
    // Integer result = template.queryForObject("select 2", Integer.class);
    // Assertions.assertEquals(Integer.valueOf(2), result);
    // // SqlRowSet result = template.queryForRowSet("select 'a' name, '1' code from dual");
    // // System.out.println(result);
    // }

}
