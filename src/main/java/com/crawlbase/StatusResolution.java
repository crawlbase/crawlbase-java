package com.crawlbase;

import java.util.Map;

/**
 * Resolves the Crawlbase status of a response.
 * <p>
 * The Crawling API reports its own status in the {@code cb_status} response header
 * (or JSON field when {@code format=json}). Older responses used the name
 * {@code pc_status}; it is deprecated but still honoured as a fallback so that
 * existing integrations keep working during the transition.
 *
 * @see <a href="https://crawlbase.com/docs/crawling-api/response/#cb-status">cb_status documentation</a>
 */
public final class StatusResolution {

    /** Preferred status key. */
    public static final String CB_STATUS = "cb_status";
    /** Deprecated status key, kept as a fallback. */
    public static final String PC_STATUS = "pc_status";

    private StatusResolution() {}

    /**
     * Resolve the Crawlbase status from a pair of raw values.
     * {@code cbStatus} wins whenever it is present (non-null); otherwise {@code pcStatus} is used.
     * A present-but-empty {@code cb_status} is returned as-is and does not fall back.
     *
     * @param cbStatus value of {@code cb_status}, or {@code null} if absent
     * @param pcStatus value of {@code pc_status}, or {@code null} if absent
     * @return the resolved status, or {@code null} if neither is present
     */
    public static String resolve(String cbStatus, String pcStatus) {
        return cbStatus != null ? cbStatus : pcStatus;
    }

    /**
     * Resolve the Crawlbase status from a header/JSON map.
     * A {@code cb_status} key wins whenever it is present; otherwise {@code pc_status} is used.
     *
     * @param source map of response headers or parsed JSON fields (may be {@code null})
     * @return the resolved status, or {@code null} if neither key is present
     */
    public static String resolve(Map<String, String> source) {
        if (source == null) {
            return null;
        }
        if (source.containsKey(CB_STATUS)) {
            return source.get(CB_STATUS);
        }
        return source.get(PC_STATUS);
    }
}
