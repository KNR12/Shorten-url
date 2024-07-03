package springboot.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.demo.model.Url;
import springboot.demo.repository.UrlRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;




@Service
public class UrlServiceImpl implements UrlService{

    @Autowired
    private UrlRepository urlRepository;
    public static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_URL_LENGTH = 8;

    public String shortenUrl(String originalUrl) {
        String shortUrl = generateUniqueShortUrl();
        Timestamp expiresAt = Timestamp.valueOf(LocalDateTime.now().plusMonths(10));
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setExpiresAt(expiresAt);
        urlRepository.save(url);
        return shortUrl;
    }

    public boolean updateUrl(String shortUrl, String newOriginalUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            url.setOriginalUrl(newOriginalUrl);
            urlRepository.save(url);
            return true;
        }
        return false;
    }

    public String getOriginalUrl(String shortUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        return urlOptional.map(Url::getOriginalUrl).orElse(null);
    }

    public boolean updateExpiry(String shortUrl, int daysToAdd) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            Timestamp newExpiry = Timestamp.valueOf(url.getExpiresAt().toLocalDateTime().plusDays(daysToAdd));
            url.setExpiresAt(newExpiry);
            urlRepository.save(url);
            return true;
        }
        return false;
    }

    private String generateUniqueShortUrl() {
        String shortUrl;
        do {
            shortUrl = generateShortUrl();
        } while (urlRepository.findByShortUrl(shortUrl).isPresent());
        return shortUrl;

    }
    private String generateShortUrl() {
        StringBuilder shortUrl = new StringBuilder(SHORT_URL_LENGTH);
        Random random = new Random();
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int index = random.nextInt(BASE62.length());
            shortUrl.append(BASE62.charAt(index));
        }
        return shortUrl.toString();
    }
}

