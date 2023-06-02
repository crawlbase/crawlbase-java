package com.crawlbase;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/** 
 * Acts as wrapper for Crawlbase Crawling API.
 * @see <a href="https://crawlbase.com/docs/crawling-api">Crawlbase Crawling API documentation</a>
 * 
 * @author Crawlbase
 */
public class API 
{
    private static final String INVALID_TOKEN = "Token is required";
    private static final String INVALID_URL = "URL is required";

    private static final String OPTION_KEY_FORMAT = "format";
    private static final String OPTION_KEY_TOKEN = "token";
    private static final String OPTION_KEY_URL = "url";

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    protected static final String FORMAT_JSON = "json";

    private String token;
    private String body;
    private int statusCode;
    private String originalStatus;
    private String crawlbaseStatus;
    private String url;

    /**
     * @param token
     * Accepts a Normal token or Javascript token
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters/#token">token documentation</a>
     * @see <a href="https://crawlbase.com/login">Crawlbase site for your token</a>
     */
    public API(String token) {
        if (token == null || token.isEmpty() || token.trim().isEmpty()) {
            throw new RuntimeException(INVALID_TOKEN);
        }
        this.token = token;
    }

    /**
     * @return Authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return JSON string or the html string of the page depending on the option you selected with the format parameter (default is html).
     * @see <a href="https://crawlbase.com/docs/crawling-api/response/#body">body documentation</a>
     */
    public String getBody() {
        return body;
    }

    /**
     * @return Http code response for the request.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return The status response that we (Crawlbase) receive when crawling the url sent in the request.
     * @see <a href="https://crawlbase.com/docs/crawling-api/response/#original-status">original status documentation</a>
     */
    public String getOriginalStatus() {
        return originalStatus;
    }

    /**
     * @return The Crawlbase (pc) status code can be any status code and it's the code that ends up being valid.
     * @see <a href="https://crawlbase.com/docs/crawling-api/response/#pc-status">pc status documentation</a>
     */
    public String getCrawlbaseStatus() {
        return crawlbaseStatus;
    }

    /**
     * @return The original url that was sent in the request or the url of the redirect that Crawlbase followed.
     * @see <a href="https://crawlbase.com/docs/crawling-api/response/#url">url documentation</a>
     */
    public String getUrl() {
        return url;
    }

    /**
     * Makes a GET request to the Crawling API.
     * 
     * @param url
     * This parameter is required for all calls
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters/#url">url documentation</a>
     * 
     * @param options
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters">parameters documentation</a>
     */
    public void get(String url, Map<String, Object> options) {
        if (url == null || url.isEmpty() || url.trim().isEmpty()) {
            throw new RuntimeException(INVALID_URL);
        }
        this.url = url;
        if (options == null) {
            options = new HashMap<String, Object>();
        }
        String format = null;
        if (options.containsKey(OPTION_KEY_FORMAT) && options.get(OPTION_KEY_FORMAT) != null) {
            format = options.get(OPTION_KEY_FORMAT).toString();
        }
        URL uri = prepareURI(url, options);
        try {
            HttpURLConnection httpConn = (HttpURLConnection) uri.openConnection();
            httpConn.setRequestMethod(HTTP_METHOD_GET);
            this.statusCode = httpConn.getResponseCode();
            prepareResponse(httpConn, format);
        } catch (ProtocolException pe) {
            throw new RuntimeException(pe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    /**
     * @see API#get(String, MapString, Object>) get
     */
    public void get(String url) {
        get(url, null);
    }

    /**
     * Makes a POST request to the Crawling API.
     * 
     * @param url
     * This parameter is required for all calls
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters/#url">url documentation</a>
     * 
     * @param data
     * The data that you want to send via POST
     * 
     * @param options
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters">parameters documentation</a>
     */
    public void post(String url, Map<String, Object> data, Map<String, Object> options) {
        if (url == null || url.isEmpty() || url.trim().isEmpty()) {
            throw new RuntimeException(INVALID_URL);
        }
        if (options == null) {
            options = new HashMap<String, Object>();
        }
        if (data == null) {
            data = new HashMap<String, Object>();
        }
        String format = null;
        if (options.containsKey(OPTION_KEY_FORMAT) && options.get(OPTION_KEY_FORMAT) != null) {
            format = options.get(OPTION_KEY_FORMAT).toString();
        }
        URL uri = prepareURI(url, options);
        try {
            HttpURLConnection httpConn = (HttpURLConnection) uri.openConnection();
            httpConn.setRequestMethod(HTTP_METHOD_POST);
            setupPostRequest(httpConn, data, format);
            this.statusCode = httpConn.getResponseCode();
            prepareResponse(httpConn, format);
        } catch (ProtocolException pe) {
            throw new RuntimeException(pe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    /**
     * @see API#post(String, MapString, Object>, MapString, Object>) post
     */
    public void post(String url, Map<String, Object> data) {
        post(url, data, null);
    }

    protected String getBaseUrl() {
        return "https://api.crawlbase.com";
    }

    protected URL prepareURI(String url, Map<String, Object> options) {
        URL uri = null;
        StringBuilder pcUrl = new StringBuilder(getBaseUrl());
        pcUrl.append("?");
        pcUrl.append(String.format("token=%s", encodeQueryValue(this.token)));
        pcUrl.append("&");
        pcUrl.append(String.format("url=%s", encodeQueryValue(url)));
        if (options.size() > 0) {
            pcUrl.append("&");
            if (options.containsKey(OPTION_KEY_TOKEN)) {
                options.remove(OPTION_KEY_TOKEN);
            }
            if (options.containsKey(OPTION_KEY_URL)) {
                options.remove(OPTION_KEY_URL);
            }
            ArrayList<String> queries = new ArrayList<String>();
            for (String key : options.keySet()) {
                Object value = options.get(key);
                if (value == null) {
                    value = "";
                }
                queries.add(String.format("%s=%s", key, encodeQueryValue(value.toString())));
            }
            pcUrl.append(String.join("&", queries));
        }
        try {
            uri = new URL(pcUrl.toString());
        } catch (MalformedURLException murle) {
            throw new RuntimeException(murle.getMessage());
        }
        return uri;
    }

    protected void prepareResponse(HttpURLConnection httpConn, String format) throws IOException {
        if (format == FORMAT_JSON) {
            Map<String, String> map = getResponseBodyAsMap(httpConn);
            try { extractHeaderFromMap(map); } catch (Exception e) {}
            this.body = map.get("body").toString();
        } else {
            try { extractHeaderFromResponse(httpConn); } catch (Exception e) {}
            this.body = getResponseBody(httpConn);
        }
    }

    protected void extractHeaderFromMap(Map<String, String> map) {
        this.originalStatus = map.get("original_status");
        this.crawlbaseStatus = map.containsKey("cb_status") ? map.get("cb_status") : map.get("pc_status");
        this.url = map.get("url");
    }

    protected void extractHeaderFromResponse(HttpURLConnection httpConn) {
        this.originalStatus = httpConn.getHeaderField("original_status");
        this.crawlbaseStatus = (null == httpConn.getHeaderField("cb_status")) ? httpConn.getHeaderField("pc_status") : httpConn.getHeaderField("cb_status");
        this.url = httpConn.getHeaderField("url");
    }

    protected String getResponseBody(HttpURLConnection httpConn) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    protected Map<String, String> getResponseBodyAsMap(HttpURLConnection httpConn) throws IOException {
        String jsonResponseBody = getResponseBody(httpConn);
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        try {
            return mapper.readValue(jsonResponseBody, typeRef);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException(jpe.getMessage());
        }
    }

    private String encodeQueryValue(String queryValue) {
        try {
            return URLEncoder.encode(queryValue, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee.getMessage());
        }
    }

    private void setupPostRequest(HttpURLConnection httpConn, Map<String, Object> data, String format) throws IOException {
        httpConn.setDoOutput(true);
        DataOutputStream outputStream = null;
        String contentType = null;
        byte[] postData = null;
        if (format == FORMAT_JSON) {
            contentType = "application/json";
            postData = getJsonPostData(data);
        } else {
            contentType = "application/x-www-form-urlencoded";
            postData = getFormPostData(data);
        }
        int postDataLength = postData.length;
        httpConn.setRequestProperty("Content-Type", contentType); 
        httpConn.setRequestProperty("charset", "utf-8");
        httpConn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
        httpConn.setUseCaches(false);
        try {
            outputStream = new DataOutputStream(httpConn.getOutputStream());
            outputStream.write(postData);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private byte[] getJsonPostData(Map<String, Object> data) throws IOException {
        String json = new ObjectMapper().writeValueAsString(data);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getFormPostData(Map<String, Object> data) throws IOException {
        String postDataString = null;
        ArrayList<String> postDatas = new ArrayList<String>();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value == null) {
                value = "";
            }
            postDatas.add(String.format("%s=%s", key, encodeQueryValue(value.toString())));
        }
        postDataString = String.join("&", postDatas);
        return postDataString.getBytes(StandardCharsets.UTF_8);
    }
}
