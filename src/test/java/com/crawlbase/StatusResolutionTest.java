package com.crawlbase;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Pure tests of the cb_status / pc_status resolution logic. No network, no token.
 */
public class StatusResolutionTest {

    private static Map<String, String> map(String... kv) {
        Map<String, String> m = new HashMap<String, String>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }

    @Test
    public void resolveOnlyCbStatus() {
        assertEquals("200", StatusResolution.resolve(map("cb_status", "200")));
        assertEquals("200", StatusResolution.resolve("200", null));
    }

    @Test
    public void resolveOnlyPcStatusFallsBack() {
        assertEquals("200", StatusResolution.resolve(map("pc_status", "200")));
        assertEquals("200", StatusResolution.resolve(null, "200"));
    }

    @Test
    public void resolveBothPrefersCbStatus() {
        assertEquals("200", StatusResolution.resolve(map("cb_status", "200", "pc_status", "503")));
        assertEquals("200", StatusResolution.resolve("200", "503"));
    }

    @Test
    public void resolveNeitherIsNull() {
        assertNull(StatusResolution.resolve(map()));
        assertNull(StatusResolution.resolve((Map<String, String>) null));
        assertNull(StatusResolution.resolve(null, null));
    }

    @Test
    public void resolveEmptyCbStatusDoesNotFallBack() {
        assertEquals("", StatusResolution.resolve(map("cb_status", "", "pc_status", "200")));
        assertEquals("", StatusResolution.resolve("", "200"));
    }

    @Test
    public void apiExposesResolvedStatusThroughAllGetters() {
        API api = new API("test-token") {
            {
                extractHeaderFromMap(map("original_status", "200", "pc_status", "503", "url", "https://example.com"));
            }
        };
        assertEquals("503", api.getCbStatus());
        assertEquals("503", api.getCrawlbaseStatus());
        assertEquals("503", api.getPcStatus());
        assertEquals("200", api.getOriginalStatus());
        assertEquals("https://example.com", api.getUrl());

        API api2 = new API("test-token") {
            {
                extractHeaderFromMap(map("original_status", "200", "cb_status", "200", "pc_status", "503", "url", "https://example.com"));
            }
        };
        assertEquals("200", api2.getCbStatus());
        assertEquals("200", api2.getPcStatus());
    }
}
