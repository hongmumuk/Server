package hongmumuk.hongmumuk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hongmumuk.hongmumuk.entity.Blog;
import hongmumuk.hongmumuk.entity.Restaurant;
import hongmumuk.hongmumuk.repository.BlogRepository;
import hongmumuk.hongmumuk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulerService {

    private final BlogRepository blogRepository;
    private final RestaurantRepository restaurantRepository;

    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    private String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

    // 매달 1일 00:00에 진행 "0 0 0 1 * *"
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul" )
    @Transactional
    public void updateAll() {
        // 모든 데이터 지우고 시작
        blogRepository.deleteAll();
        // 블로그 정보 가져오기

        List<Restaurant> restaurantList = restaurantRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Restaurant restaurant : restaurantList) {
            String text = "홍대" + restaurant.getName();
            String query = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String apiURL = "https://openapi.naver.com/v1/search/blog?query=" + query;

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);
            String responseBody = get(apiURL, requestHeaders);

            try {
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode items = root.path("items");

                for (JsonNode item : items) {
                    String title = item.path("title").asText().replaceAll("<.*?>", "");
                    String url = item.path("link").asText();
                    String subTitle = item.path("description").asText().replaceAll("<.*?>", "");
                    String postDate = item.path("postdate").asText();
                    String bloggerName = item.path("bloggername").asText();

                    Blog blog = new Blog(title, subTitle, url, postDate, bloggerName, restaurant);
                    blogRepository.save(blog);

                }
            } catch (IOException e) {
                throw new RuntimeException("JSON 파싱 오류", e);
            }
        }
    }
}
