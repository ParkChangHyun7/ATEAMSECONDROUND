package seoul.its.info.common.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class SeoulPublicJsonScheduler {

    @Value("${open.api.base.key}")
    private String openApiKey;

    private final String API_URL = "http://openapi.seoul.go.kr:8088/%s/json/citydata/1/5/%s";
    private final String AREA = "%EC%A2%85%EB%A1%9C%C2%B7%EC%B2%AD%EA%B3%84%20%EA%B4%80%EA%B4%91%ED%8A%B9%EA%B5%AC"; // 종로·청계 관광특구 인코딩
    private final String JSON_FILE_PATH = "src/main/data/api/json/seoulpublicdata.json";

    private final HttpClient httpClient;

    public SeoulPublicJsonScheduler() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @PostConstruct
    public void init() {
        log.info("서울시 공공데이터 스케줄러 초기화");
        
        Path filePath = Path.of(JSON_FILE_PATH);
        if (!Files.exists(filePath)) {
            log.info("JSON 파일이 없어 새로 생성합니다.");
            fetchAndSaveData();
            return;
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            Instant fileCreationTime = attrs.lastModifiedTime().toInstant();
            Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

            if (fileCreationTime.isBefore(oneHourAgo)) {
                log.info("JSON 파일이 1시간 이상 지났습니다. 새로운 데이터를 가져옵니다.");
                fetchAndSaveData();
            } else {
                log.info("최근 1시간 이내에 생성된 JSON 파일이 존재합니다. 기존 파일을 사용합니다.");
            }
        } catch (IOException e) {
            log.error("파일 속성을 확인하는 중 오류 발생", e);
            fetchAndSaveData();
        }
    }

    // 매 시간 정각에 실행
    @Scheduled(cron = "0 0 * * * *")
    public void fetchAndSaveData() {
        try {
            String url = String.format(API_URL, openApiKey, AREA);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // JSON 파일 저장
                Path filePath = Path.of(JSON_FILE_PATH);
                // 디렉토리가 없으면 생성
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, response.body(), 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);
                
                log.info("서울시 공공데이터 JSON 파일 업데이트 완료: {}", JSON_FILE_PATH);
            } else {
                log.error("서울시 공공데이터 API 호출 실패. 상태 코드: {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("서울시 공공데이터 처리 중 오류 발생", e);
        }
    }
} 