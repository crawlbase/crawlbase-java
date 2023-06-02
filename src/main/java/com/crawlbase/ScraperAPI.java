package com.crawlbase;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/** 
 * Acts as wrapper for Crawlbase Scraper API.
 * @see <a href="https://crawlbase.com/docs/scraper-api">Crawlbase Scraper API documentation</a>
 * 
 * @author Crawlbase
 */
public class ScraperAPI extends API
{
    private int remainingRequests;

    /**
     * @param token
     * Accepts a Normal token or Javascript token
     * @see <a href="hhttps://crawlbase.com/docs/scraper-api/parameters/#token">token documentation</a>
     * @see <a href="https://crawlbase.com/login">Crawlbase site for your token</a>
     */
    public ScraperAPI(String token) {
        super(token);
    }

    /**
     * @return The number of requests that are left in your subscription plan.
     * @see <a href="https://crawlbase.com/docs/scraper-api/response/#remaining-requests">remaining requests documentation</a>
     */
    public int getRemainingRequests() {
        return remainingRequests;
    }

    /**
     * This method is disabled and will always throws {@link RuntimeException RuntimeException}
     */
    @Override
    public void post(String url, Map<String, Object> data, Map<String, Object> options) {
        throw new RuntimeException("Only GET is allowed for the ScraperAPI");
    }

    @Override
    protected String getBaseUrl() {
        return "https://api.crawlbase.com/scraper";
    }

    @Override
    protected void prepareResponse(HttpURLConnection httpConn, String format) throws IOException {
        super.prepareResponse(httpConn, FORMAT_JSON);
    }

    @Override
    protected void extractHeaderFromMap(Map<String, String> map) {
        this.remainingRequests = Integer.parseInt(map.get("remaining_requests"));
    }

    @Override
    protected Map<String, String> getResponseBodyAsMap(HttpURLConnection httpConn) throws IOException {
        String jsonResponseBody = getResponseBody(httpConn);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap> typeRef = new TypeReference<HashMap>() {};
        try {
            Map tempMap = mapper.readValue(jsonResponseBody, typeRef);
            Map<String, String> returnMap = new HashMap<String, String>();
            for (Object key : tempMap.keySet()) {
                Object value = tempMap.get(key);
                if (key.toString().equals("body")) {
                    value = new ObjectMapper().writeValueAsString(value);
                }
                returnMap.put(key.toString(), value.toString());
            }
            return returnMap;
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException(jpe.getMessage());
        }
    }
}
