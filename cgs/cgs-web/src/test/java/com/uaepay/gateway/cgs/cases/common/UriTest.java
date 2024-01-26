package com.uaepay.gateway.cgs.cases.common;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

public class UriTest {

    @Test
    public void test() throws URISyntaxException {
        assertDomain("http://abc.com/1?p=1", "abc.com");
        assertDomain("https://abc.com/1?p=1", "abc.com");
        assertDomain("https://abc.com/a/b/c", "abc.com");
        assertDomain("https://abc.com/?p=1", "abc.com");
        assertDomain("https://abc.com", "abc.com");
        assertDomain("https://abc.com:8080/?p=1", "abc.com:8080");
        assertDomain("abc.com", null);
        assertDomain("", null);
    }

    public void assertDomain(String referer, String expectDomain) throws URISyntaxException {
        URI uri = new URI(referer);
        Assert.assertEquals(expectDomain, uri.getAuthority());
    }

}
