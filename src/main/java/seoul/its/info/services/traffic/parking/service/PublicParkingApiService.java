package seoul.its.info.services.traffic.parking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 서울시 공공데이터포털의 공영/민영 주차장 API를 호출하고
 * 중복 데이터를 병합해 반환하는 서비스 클래스
 */
@Service
public class PublicParkingApiService {

    @Value("${seoul.parking.api.key}")
    private String apiKey;

    private static final int BATCH_SIZE = 1000; // 한 번에 가져올 데이터 수
    private static final int MAX_TOTAL = 5000;  // 최대 5000개까지 시도

    public String getParkingData() throws Exception {
        List<Map<String, Object>> allParkingData = new ArrayList<>();
        
        String baseUrl = "http://openapi.seoul.go.kr:8088/" + apiKey + "/json/GetParkInfo/";
        
        // 여러 번 API 호출하여 더 많은 데이터 수집
        for (int start = 1; start <= MAX_TOTAL; start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE - 1, MAX_TOTAL);
            String apiUrl = baseUrl + start + "/" + end + "/";
            
            try {
                System.out.println("API 호출: " + apiUrl);
                List<Map<String, Object>> batchData = callParkingApi(apiUrl);
                
                if (batchData.isEmpty()) {
                    System.out.println("더 이상 데이터가 없습니다. 총 " + allParkingData.size() + "개 수집완료");
                    break;
                }
                
                allParkingData.addAll(batchData);
                System.out.println("배치 " + start + "-" + end + ": " + batchData.size() + "개 추가, 총 " + allParkingData.size() + "개");
                
            } catch (Exception e) {
                System.err.println("API 호출 실패 (" + start + "-" + end + "): " + e.getMessage());
                // 에러가 발생해도 이미 수집한 데이터는 사용
                break;
            }
        }

        System.out.println("최종 수집된 주차장 데이터: " + allParkingData.size() + "개");

        // 중복 제거 로직
        Map<String, Map<String, Object>> mergedMap = new LinkedHashMap<>();
        for (Map<String, Object> lot : allParkingData) {
            String name = String.valueOf(lot.getOrDefault("PKLT_NM", "")).trim();
            String addr = String.valueOf(lot.getOrDefault("ADDR", "")).trim();
            String key = name + "-" + addr;

            if (mergedMap.containsKey(key)) {
                Map<String, Object> existing = mergedMap.get(key);

                // 공간 수 누적
                int prevCount = parseInt(existing.get("TPKCT"));
                int newCount = parseInt(lot.get("TPKCT"));
                existing.put("TPKCT", prevCount + newCount);

                // 기타 값 채우기
                fillIfEmpty(existing, lot, "TELNO");
                fillIfEmpty(existing, lot, "LAT");
                fillIfEmpty(existing, lot, "LOT");
                fillIfEmpty(existing, lot, "WD_OPER_BGNG_TM");
                fillIfEmpty(existing, lot, "WD_OPER_END_TM");

                if (parseInt(existing.get("PRK_CRG")) == 0 && parseInt(lot.get("PRK_CRG")) > 0) {
                    existing.put("PRK_CRG", lot.get("PRK_CRG"));
                }

            } else {
                mergedMap.put(key, new LinkedHashMap<>(lot));
            }
        }

        System.out.println("중복 제거 후 최종 주차장 수: " + mergedMap.size() + "개");

        // 최종 JSON 구조로 변환
        Map<String, Object> finalJson = new HashMap<>();
        finalJson.put("GetParkInfo", Map.of("row", new ArrayList<>(mergedMap.values())));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(finalJson);
    }

    // 개별 API 호출 메서드
    private List<Map<String, Object>> callParkingApi(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000); // 10초 타임아웃
        conn.setReadTimeout(10000);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        reader.close();
        conn.disconnect();

        String jsonResponse = sb.toString();

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode rowNode = root.path("GetParkInfo").path("row");

        if (rowNode.isMissingNode() || !rowNode.isArray()) {
            return new ArrayList<>();
        }

        return mapper.convertValue(rowNode, new TypeReference<List<Map<String, Object>>>() {});
    }

    // 유틸 메서드: null-safe 문자열 채우기
    private void fillIfEmpty(Map<String, Object> target, Map<String, Object> source, String key) {
        Object current = target.get(key);
        Object candidate = source.get(key);
        if ((current == null || current.toString().isBlank()) && candidate != null) {
            target.put(key, candidate);
        }
    }

    private int parseInt(Object obj) {
        if (obj == null) return 0;
        try {
            return Integer.parseInt(obj.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
