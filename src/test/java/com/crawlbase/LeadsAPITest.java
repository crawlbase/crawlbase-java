package com.crawlbase;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class LeadsAPITest 
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void itThrowsExceptionWhenTokenIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new LeadsAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenTokenIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new LeadsAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenDomainIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Domain is required");
        LeadsAPI api = new LeadsAPI("testtoken");
        api.get(null);
    }

    @Test
    public void itThrowsExceptionWhenDomainIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Domain is required");
        LeadsAPI api = new LeadsAPI("testtoken");
        api.get("");
    }

    @Test
    public void itAssignsToken() {
        LeadsAPI api = new LeadsAPI("testtoken");
        assertEquals(api.getToken(), "testtoken");
    }
}
