package springboot.demo.service;

public interface UrlService {
    String shortenUrl(String originalUrl);

    boolean updateUrl(String shortUrl, String newOriginalUrl);

    String getOriginalUrl(String shortUrl);

    boolean updateExpiry(String shortUrl, int daysToAdd);
}


