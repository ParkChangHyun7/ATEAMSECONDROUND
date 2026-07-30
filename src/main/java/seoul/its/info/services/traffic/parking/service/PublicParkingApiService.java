package seoul.its.info.services.traffic.parking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final String API_KEY = "674e6e41676368613734506e46676d";
    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/" + API_KEY + "/json/GetParkInfo/1/1000/";

    public String getParkingData() throws Exception {
        // 1. API 호출
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

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

        String originalJson = sb.toString();

        // 2. JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(originalJson);
        JsonNode rowNode = root.path("GetParkInfo").path("row");

        List<Map<String, Object>> originalList = mapper.convertValue(
                rowNode, new TypeReference<List<Map<String, Object>>>() {}
        );

        // 3. 중복 제거 로직 (PKLT_NM + ADDR 기준으로 병합)
        Map<String, Map<String, Object>> mergedMap = new LinkedHashMap<>();
        for (Map<String, Object> lot : originalList) {
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

        // 4. 다시 JSON 구조로 변환
        Map<String, Object> finalJson = new HashMap<>();
        finalJson.put("GetParkInfo", Map.of("row", new ArrayList<>(mergedMap.values())));

        return mapper.writeValueAsString(finalJson);
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
