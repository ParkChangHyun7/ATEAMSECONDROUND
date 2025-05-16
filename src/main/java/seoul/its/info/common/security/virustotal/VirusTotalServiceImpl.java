package seoul.its.info.common.security.virustotal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;

//실제 사용되는 apiKey 값과 .Mono를 unused, never used로 IDE에서 인식하고 있어서
// SuspressWarnings로 경고 지워버림. IDE에서 추적 못해서 생긴 경고이니 무시해도 됨.
@SuppressWarnings("unused")
@Service
@Slf4j
public class VirusTotalServiceImpl implements VirusTotalService {

   private final WebClient webClient;
   private final String apiKey;

   private final int maxPollingAttempts = 12;
   private final Duration pollingInterval = Duration.ofSeconds(5);

   public VirusTotalServiceImpl(WebClient.Builder webClientBuilder,
         @Value("${virustotal.api.key}") String apiKey,
         @Value("${virustotal.api.base-url:https://www.virustotal.com/api/v3}") String baseUrl) {
      this.apiKey = apiKey;
      this.webClient = webClientBuilder.baseUrl(baseUrl)
            .defaultHeader("x-apikey", apiKey)
            .build();
      log.info("VirusTotalService initialized with base URL: {}", baseUrl);
   }

   @Override
   public VirusTotalScanResult scanFile(InputStream inputStream) throws IOException, InterruptedException {
      log.info("VirusTotal 파일 스캔 시작 (InputStream)");

      String originalFilename = "uploaded_file";

      String analysisId = uploadFile(inputStream, originalFilename);
      if (analysisId == null) {
         log.error("VirusTotal 파일 업로드 실패 (InputStream): {}", originalFilename);
         throw new IOException("VirusTotal 파일 업로드 실패");
      }
      log.info("VirusTotal 파일 업로드 완료, Analysis ID: {}", analysisId);

      VirusTotalAnalysisReport report = getAnalysisReport(analysisId);
      if (report == null) {
         log.error("VirusTotal 분석 결과 조회 실패: Analysis ID={}", analysisId);
         throw new IOException("VirusTotal 분석 결과 조회 실패");
      }

      int maliciousCount = extractMaliciousCount(report);
      VirusTotalRiskLevel riskLevel = determineRiskLevel(maliciousCount);
      log.info("VirusTotal 스캔 완료: {}, Malicious Count: {}, Risk Level: {}",
            originalFilename, maliciousCount, riskLevel);

      return new VirusTotalScanResult(riskLevel, report, maliciousCount);
   }

   private String uploadFile(InputStream inputStream, String filename) throws IOException {
      MultipartBodyBuilder builder = new MultipartBodyBuilder();

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, nRead);
      }
      buffer.flush();
      byte[] fileBytes = buffer.toByteArray();

      builder.part("file", new ByteArrayResource(fileBytes))
            .filename(filename);

      try {
         FileUploadResponse response = webClient.post()
               .uri("/files")
               .contentType(MediaType.MULTIPART_FORM_DATA)
               .body(BodyInserters.fromMultipartData(builder.build()))
               .retrieve()
               .bodyToMono(FileUploadResponse.class)
               .block();

         if (response != null && response.data != null && response.data.id != null) {
            return response.data.id;
         } else {
            log.error("VirusTotal 파일 업로드 응답에서 analysis ID를 찾을 수 없음: FileName={}", filename);
            return null;
         }
      } catch (WebClientResponseException e) {
         log.error("VirusTotal API 오류 (파일 업로드): Status={}, Response={}, FileName={}",
               e.getStatusCode(), e.getResponseBodyAsString(), filename, e);
         throw new IOException("VirusTotal API 오류: " + e.getStatusCode(), e);
      } catch (Exception e) {
         log.error("VirusTotal 파일 업로드 중 예상치 못한 오류: FileName={}", filename, e);
         throw new IOException("VirusTotal 파일 업로드 중 오류 발생", e);
      }
   }

   private VirusTotalAnalysisReport getAnalysisReport(String analysisId) throws InterruptedException, IOException {
      for (int attempt = 1; attempt <= maxPollingAttempts; attempt++) {
         log.debug("VirusTotal 분석 결과 조회 시도 {}/{} : ID={}", attempt, maxPollingAttempts, analysisId);
         try {
            VirusTotalAnalysisReport report = webClient.get()
                  .uri("/analyses/{id}", analysisId)
                  .retrieve()
                  .bodyToMono(VirusTotalAnalysisReport.class)
                  .block();

            if (report != null && report.data != null && report.data.attributes != null) {
               String status = report.data.attributes.status;
               log.debug("VirusTotal 분석 상태: {}, ID={}", status, analysisId);
               if ("completed".equalsIgnoreCase(status)) {
                  return report; // 분석 완료, 결과 반환
               } else if ("queued".equalsIgnoreCase(status) || "inprogress".equalsIgnoreCase(status)) {
                  // 분석 중, 잠시 대기 후 재시도
                  Thread.sleep(pollingInterval.toMillis());
               } else {
                  // 예상치 못한 상태 (예: failed)
                  log.error("VirusTotal 분석 실패 또는 알 수 없는 상태: Status={}, ID={}", status, analysisId);
                  return null; // 실패로 간주
               }
            } else {
               log.warn("VirusTotal 분석 결과 응답이 비정상적입니다. ID={}", analysisId);
               // 잠시 후 재시도할 수 있도록 null 반환 대신 계속 진행
               Thread.sleep(pollingInterval.toMillis());
            }
         } catch (WebClientResponseException e) {
            // 404 Not Found는 분석이 아직 생성되지 않았을 수 있음을 의미할 수 있음 (잠시 후 재시도)
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
               log.warn("VirusTotal 분석 결과를 아직 찾을 수 없습니다 (404). ID={}", analysisId);
               Thread.sleep(pollingInterval.toMillis());
            } else {
               log.error("VirusTotal API 오류 (분석 결과 조회): Status={}, Response={}, ID={}",
                     e.getStatusCode(), e.getResponseBodyAsString(), analysisId, e);
               throw new IOException("VirusTotal API 오류 (분석 결과 조회): " + e.getStatusCode(), e);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            log.error("VirusTotal 분석 결과 대기 중 인터럽트 발생. ID={}", analysisId, e);
            throw e;
         } catch (Exception e) {
            log.error("VirusTotal 분석 결과 조회 중 예상치 못한 오류: ID={}", analysisId, e);
            throw new IOException("VirusTotal 분석 결과 조회 중 오류 발생", e);
         }
      }
      log.error("VirusTotal 분석 시간 초과 ({}번 시도). ID={}", maxPollingAttempts, analysisId);
      return null; // 최대 시도 횟수 초과
   }

   private int extractMaliciousCount(VirusTotalAnalysisReport report) {
      if (report == null || report.data == null || report.data.attributes == null
            || report.data.attributes.stats == null) {
         log.warn("VirusTotal 분석 결과에서 통계 정보를 찾을 수 없습니다.");
         return 0; // 통계 정보 없으면 0 반환 (안전하다고 가정하지 않도록 주의)
      }
      return report.data.attributes.stats.malicious;
   }

   private VirusTotalRiskLevel determineRiskLevel(int maliciousCount) {
      if (maliciousCount <= 1) {
         return VirusTotalRiskLevel.SAFE;
      } else if (maliciousCount <= 5) {
         return VirusTotalRiskLevel.SUSPICIOUS;
      } else {
         return VirusTotalRiskLevel.DANGER;
      }
   }
}