package seoul.its.info.services.traffic.parking.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *  서울시 공공데이터포털의 공영/민영 주차장 API를 호출하는 서비스 클래스
 * - 외부 API로부터 실시간 데이터를 받아오기 위한 역할 수행
 * - JSON 응답 문자열을 반환
 */
@Service
public class PublicParkingApiService {

    //  발급받은 인증키를 아래에 복사해서 붙여넣기 (테스트용이면 제한된 요청만 가능함)
    private static final String API_KEY = "674e6e41676368613734506e46676d";

    // 호출할 API의 URL - XML이 아닌 JSON 응답 포맷을 요청 (json 타입 지정 중요!)
    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/674e6e41676368613734506e46676d/json/GetParkInfo/1/1000/";

    /**
     *  주차장 정보를 가져오는 메서드
     * - 외부 HTTP API 호출
     * - JSON 문자열을 통째로 반환
     */
    public String getParkingData() throws Exception {
        // 1. URL 객체 생성
        URL url = new URL(BASE_URL);

        // 2. HttpURLConnection 열기 (GET 방식)
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // 3. 응답 스트림 읽기 (UTF-8로 인코딩)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );

        // 4. 응답 라인을 한 줄씩 읽어 문자열로 누적
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // 5. 스트림 및 연결 종료
        reader.close();
        conn.disconnect();

        // 6. JSON 문자열 반환
        return sb.toString();
    }
}