# Crawlbase

Java library for scraping and crawling websites using the Crawlbase API.

## Crawling API Usage

Import the needed libraries in your project

```java
import java.util.*;
import com.crawlbase.*;
```

Initialize the API with one of your account tokens, either normal or javascript token. Then make get or post requests accordingly.

You can get a token for free by [creating a Crawlbase account](https://crawlbase.com/signup) and 1000 free testing requests. You can use them for tcp calls or javascript calls or both.

```java
API api = new API("YOUR_TOKEN");
```

Please note that all the api methods does not throw checked exceptions. All exceptions thrown are `RuntimeException`.

### GET requests

Pass the url that you want to scrape plus any options from the ones available in the [API documentation](https://crawlbase.com/dashboard/docs).

```java
api.get(url, options);
```

Example:

```java
api.get("https://www.facebook.com/britneyspears");
System.out.println(api.getStatusCode());
System.out.println(api.getOriginalStatus());
System.out.println(api.getCrawlbaseStatus());
System.out.println(api.getBody());
```

You can pass any options of what the Crawlbase API supports in exact param format.

Example:

```java
HashMap<String, Object> options = new HashMap<String, Object>();
options.put("user_agent", "Mozilla/5.0 (Windows NT 6.2; rv:20.0) Gecko/20121202 Firefox/30.0");
options.put("format", "json");

api.get("https://www.reddit.com/r/pics/comments/5bx4bx/thanks_obama/", options);

System.out.println(api.getStatusCode());
System.out.println(api.getBody());
```

### POST requests

Pass the url that you want to scrape, the data that you want to send which can be either a json or a string, plus any options from the ones available in the [API documentation](https://crawlbase.com/dashboard/docs).

```java
api.post(url, data, options);
```

Example:

```java
HashMap<String, Object> data = new HashMap<String, Object>();
data.put("text", "example search");
api.post("https://producthunt.com/search", data);
System.out.println(api.getStatusCode());
System.out.println(api.getBody());
```

You can send the data as application/json instead of x-www-form-urlencoded by setting options `post_content_type` as json.

```java
HashMap<String, Object> data = new HashMap<String, Object>();
data.put("some_json", "with some value");

HashMap<String, Object> options = new HashMap<String, Object>();
options.put("post_content_type", "json");

api.post("https://httpbin.org/post", data, options);

System.out.println(api.getStatusCode());
System.out.println(api.getBody());
```

### Javascript requests

If you need to scrape any website built with Javascript like React, Angular, Vue, etc. You just need to pass your javascript token and use the same calls. Note that only `get` is available for javascript and not `post`.

```java
API api = new API("YOUR_JAVASCRIPT_TOKEN");
```

```java
api.get("https://www.nfl.com");
System.out.println(api.getStatusCode());
System.out.println(api.getBody());
```

Same way you can pass javascript additional options.

```java
HashMap<String, Object> options = new HashMap<String, Object>();
options.put("page_wait", "5000");

api.get("https://www.freelancer.com", options);

System.out.println(api.getStatusCode());
```

## Original status

You can always get the original status and crawlbase status from the response. Read the [Crawlbase documentation](https://crawlbase.com/dashboard/docs) to learn more about those status.

```java
api.get("https://sfbay.craigslist.org/");

System.out.println(api.getOriginalStatus());
System.out.println(api.getCrawlbaseStatus());
```

## Scraper API usage

Initialize the Scraper API using your normal token and call the `get` method.

```java
ScraperAPI scraperApi = new ScraperAPI("YOUR_TOKEN");
```

Pass the url that you want to scrape plus any options from the ones available in the [Scraper API documentation](https://crawlbase.com/docs/scraper-api/parameters).

```java
scraperApi.get(url, options);
```

Example:

```java
scraperApi.get("https://www.amazon.com/Halo-SleepSack-Swaddle-Triangle-Neutral/dp/B01LAG1TOS");
System.out.println(scraperApi.getStatusCode());
System.out.println(scraperApi.getBody());
```

## Leads API usage

Initialize with your Leads API token and call the `get` method.

```java
LeadsAPI leadsApi = new LeadsAPI("YOUR_TOKEN");

leadsApi.get("stripe.com");
System.out.println(leadsApi.getStatusCode());
System.out.println(leadsApi.getBody());
```

If you have questions or need help using the library, please open an issue or [contact us](https://crawlbase.com/contact).

## Screenshots API usage

Initialize with your Screenshots API token and call the `get` method.

```java
ScreenshotsAPI screenshotsApi = new ScreenshotsAPI("YOUR_TOKEN");

screenshotsApi.get("https://www.apple.com");
System.out.println(screenshotsApi.getStatusCode());
System.out.println(screenshotsApi.getScreenshotPath());
```

or specifying a file path

```java
ScreenshotsAPI screenshotsApi = new ScreenshotsAPI("YOUR_TOKEN");

HashMap<String, Object> options = new HashMap<String, Object>();
options.put("save_to_path", "/home/my-dir/apple.jpg");
screenshotsApi.get("https://www.apple.com", options);
System.out.println(screenshotsApi.getStatusCode());
System.out.println(screenshotsApi.getScreenshotPath());
```

Note that `screenshotsApi.get(url, options)` method accepts an [options](https://crawlbase.com/docs/screenshots-api/parameters)

Also note that `screenshotsApi.getBody()` returns a Base64 string representation of the binary image file.
If you want to convert the body to bytes then you have to do the following:

```java
byte[] data = Base64.getDecoder().decode(screenshotsApi.getBody());
```

If you have questions or need help using the library, please open an issue or [contact us](https://crawlbase.com/contact).

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/crawlbase-source/crawlbase-java. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

The library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).

## Code of Conduct

Everyone interacting in the Crawlbase project’s codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](https://github.com/crawlbase-source/crawlbase-java/blob/master/CODE_OF_CONDUCT.md).

---

Copyright 2025 Crawlbase

