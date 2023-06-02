package com.crawlbase;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Base64;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.net.HttpURLConnection;

/** 
 * Acts as wrapper for Crawlbase Screenshots API.
 * @see <a href="https://crawlbase.com/docs/screenshots-api">Crawlbase Screenshots API documentation</a>
 * 
 * @author Crawlbase
 */
public class ScreenshotsAPI extends API {

    private static final String INVALID_SAVE_TO_PATH_FILENAME = "Filename must end with .jpg or .jpeg";
    private static final String SAVE_TO_PATH_FILENAME_PATTERN = ".+\\.(jpg|JPG|jpeg|JPEG)$";
    private static final String SAVE_TO_PATH_KEY = "save_to_path";

    private String screenshotPath;
    private boolean success;
    private int remainingRequests;
    private String screenshotUrl;

    /**
     * @param token
     * Accepts a Normal token
     * @see <a href="https://crawlbase.com/docs/screenshots-api/parameters/#token">token documentation</a>
     * @see <a href="https://crawlbase.com/login">Crawlbase site for your token</a>
     */
    public ScreenshotsAPI(String token) {
        super(token);
    }

    /**
     * @return The generated screenshot image file path.
     */
    public String getScreenshotPath() {
        return screenshotPath;
    }

    /**
     * @return A boolean indicating if the request was successful or not.
     * @see <a href="https://crawlbase.com/docs/screenshots-api/response/#success">success documentation</a>
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return JSON string or the html string of the page depending on the option you selected with the format parameter (default is html).
     * @see <a href="https://crawlbase.com/docs/crawling-api/response/#body">body documentation</a>
     */
    public int getRemainingRequests() {
        return remainingRequests;
    }

    /**
     * @return The url sent back by Crawlbase when you set <a href="https://crawlbase.com/docs/screenshots-api/parameters/#store">store</a> in the parameters.
     */
    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    /**
     * This method is disabled and will always throws {@link RuntimeException RuntimeException}
     */
    @Override
    public void post(String url, Map<String, Object> data, Map<String, Object> options) {
        throw new RuntimeException("Only GET is allowed for the ScreenshotsAPI");
    }

    /**
     * Makes a GET request to the Screenshots API.
     * 
     * @param url
     * This parameter is required for all calls
     * @see <a href="https://crawlbase.com/docs/crawling-api/parameters/#url">url documentation</a>
     * 
     * @param options
     * @see <a href="https://crawlbase.com/docs/screenshots-api/parameters">parameters documentation</a>
     */
    @Override
    public void get(String url, Map<String, Object> options) {
        if (options == null) {
            options = new HashMap<String, Object>();
        }
        String _screenshotPath = null;
        if (options.containsKey(SAVE_TO_PATH_KEY)) {
            _screenshotPath = options.get(SAVE_TO_PATH_KEY).toString();
            options.remove(SAVE_TO_PATH_KEY);
        } else {
            _screenshotPath = generateFilePath();
        }
        Pattern pattern = Pattern.compile(SAVE_TO_PATH_FILENAME_PATTERN);
        Matcher matcher = pattern.matcher(_screenshotPath);
        if (!matcher.matches()) {
            throw new RuntimeException(INVALID_SAVE_TO_PATH_FILENAME);
        }
        this.screenshotPath = _screenshotPath;
        super.get(url, options);
    }

    @Override
    protected String getBaseUrl() {
        return "https://api.crawlbase.com/screenshots";
    }

    @Override
    protected void prepareResponse(HttpURLConnection httpConn, String format) throws IOException {
        super.prepareResponse(httpConn, null);
    }

    @Override
    protected void extractHeaderFromResponse(HttpURLConnection httpConn) {
        this.remainingRequests = Integer.parseInt(httpConn.getHeaderField("remaining_requests"));
        this.success = httpConn.getHeaderField("success").equals("true");
        this.screenshotUrl = httpConn.getHeaderField("screenshot_url");
    }

    @Override
    protected String getResponseBody(HttpURLConnection httpConn) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String filename = this.screenshotPath;
        try {
            inputStream = httpConn.getInputStream();
            File targetFile = new File(filename);
            outputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
            inputStream.close();
            }
        }
        this.screenshotPath = filename;
        File targetFile = new File(filename);
        byte[] fileContent = Files.readAllBytes(targetFile.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String generateFileName() {
        return String.format("%s.jpg", UUID.randomUUID().toString());
    }

    private String generateFilePath() {
        return Paths.get(System.getProperty("java.io.tmpdir"), generateFileName()).toAbsolutePath().toString();
    }
}
