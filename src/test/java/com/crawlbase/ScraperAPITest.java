package com.crawlbase;

import java.util.HashMap;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ScraperAPITest 
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void itThrowsExceptionWhenTokenIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new ScraperAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenTokenIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new ScraperAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        ScraperAPI api = new ScraperAPI("testtoken");
        api.get(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        ScraperAPI api = new ScraperAPI("testtoken");
        api.get("");
    }

    @Test
    public void itAssignsToken() {
        ScraperAPI api = new ScraperAPI("testtoken");
        assertEquals(api.getToken(), "testtoken");
    }

    @Test
    public void itThrowsExceptionWhenInvokingPost()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Only GET is allowed for the ScraperAPI");
        ScraperAPI api = new ScraperAPI("testtoken");
        HashMap<String, Object> data = new HashMap<String, Object>();
        HashMap<String, Object> options = new HashMap<String, Object>();
        api.post("https://www.apple.com", data, options);
    }
}
