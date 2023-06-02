package com.crawlbase;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

/** 
 * Acts as wrapper for Crawlbase Leads API.
 * @see <a href="https://crawlbase.com/docs/leads-api">Crawlbase Leads API documentation</a>
 * 
 * @author Crawlbase
 */
public class LeadsAPI 
{
    private static final String INVALID_TOKEN = "Token is required";
    private static final String INVALID_DOMAIN = "Domain is required";

    private String token;
    private String body;
    private int statusCode;

    /**
     * @param token
     * Normal token
     * @see <a href="https://crawlbase.com/login">Crawlbase site for your token</a>
     */
    public LeadsAPI(String token) {
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
     * @return JSON string
     * @see <a href="https://crawlbase.com/docs/leads-api/response/">response documentation</a>
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
     * 
     * @param domain
     * @see <a href="https://crawlbase.com/docs/leads-api/parameters/#domain">domain documentation</a>
     */
    public void get(String domain) {
        if (domain == null || domain.isEmpty() || domain.trim().isEmpty()) {
            throw new RuntimeException(INVALID_DOMAIN);
        }     
        BufferedReader reader = null;
        StringBuilder urlStringBuilder = new StringBuilder("https://api.crawlbase.com/leads");
        try {
            urlStringBuilder.append("?token=");
            urlStringBuilder.append(this.token);
            urlStringBuilder.append("&domain=");
            urlStringBuilder.append(URLEncoder.encode(domain, "UTF-8"));
            URL url = new URL(urlStringBuilder.toString());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            this.statusCode = httpConn.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            this.body = response.toString();
        } catch (MalformedURLException murle) {
            throw new RuntimeException(murle.getMessage());
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee.getMessage());
        } catch (ProtocolException pe) {
            throw new RuntimeException(pe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) {}
            }
        }
    }
}
