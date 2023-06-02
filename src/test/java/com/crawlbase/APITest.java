package com.crawlbase;

import java.net.HttpURLConnection;

import org.junit.Test;
import org.junit.Rule;
import org.junit.Before;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class APITest 
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    public HttpURLConnection httpURLConnection;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void itThrowsExceptionWhenTokenIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new API(null);
    }

    @Test
    public void itThrowsExceptionWhenTokenIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new API(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        API api = new API("testtoken");
        api.get(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsNullUsingPost()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        API api = new API("testtoken");
        api.post(null, null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        API api = new API("testtoken");
        api.get("");
    }

    @Test
    public void itThrowsExceptionWhenUrlIsEmptyUsingPost()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        API api = new API("testtoken");
        api.post("", null);
    }

    @Test
    public void itAssignsToken() {
        API api = new API("testtoken");
        assertEquals(api.getToken(), "testtoken");
    }
}
