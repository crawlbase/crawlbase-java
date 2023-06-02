package com.crawlbase;

import java.util.HashMap;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class ScreenshotsAPITest 
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void itThrowsExceptionWhenTokenIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new ScreenshotsAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenTokenIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Token is required");
        new ScreenshotsAPI(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsNull()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        ScreenshotsAPI api = new ScreenshotsAPI("testtoken");
        api.get(null);
    }

    @Test
    public void itThrowsExceptionWhenUrlIsEmpty()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("URL is required");
        ScreenshotsAPI api = new ScreenshotsAPI("testtoken");
        api.get("");
    }

    @Test
    public void itAssignsToken() {
        ScreenshotsAPI api = new ScreenshotsAPI("testtoken");
        assertEquals(api.getToken(), "testtoken");
    }

    @Test
    public void itThrowsExceptionWhenInvokingPost()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Only GET is allowed for the ScreenshotsAPI");
        ScreenshotsAPI api = new ScreenshotsAPI("testtoken");
        HashMap<String, Object> data = new HashMap<String, Object>();
        HashMap<String, Object> options = new HashMap<String, Object>();
        api.post("https://www.apple.com", data, options);
    }

    @Test
    public void itThrowsExceptionWhenSaveToPathIsNotAJpeg()
    {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Filename must end with .jpg or .jpeg");
        ScreenshotsAPI api = new ScreenshotsAPI("testtoken");
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("save_to_path", "/home/test-image.jpegz");
        api.get("https://www.apple.com", options);
    }
}
