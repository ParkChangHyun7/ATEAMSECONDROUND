package seoul.its.info.services.traffic.parking.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 서울시 공공데이터포털의 공영/민영 주차장 API를 호출하는 서비스 클래스
 * - 외부 API로부터 실시간 데이터를 받아오기 위한 역할 수행
 * - JSON 응답 문자열을 반환
 */
@Service
public class PublicParkingApiService {

    // 발급받은 인증키 (본인의 인증키로 대체 가능)
    private static final String API_KEY = "674e6e41676368613734506e46676d";

    // JSON 형식으로 응답받는 URL (1~1000건 요청)
    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/" + API_KEY + "/json/GetParkInfo/1/1000/";

    /**
     * 주차장 정보를 가져오는 메서드
     * - 외부 HTTP API 호출
     * - JSON 문자열을 통째로 반환
     */
    public String getParkingData() throws Exception {
        // 1. URL 객체 생성
        URL url = new URL(BASE_URL);

        // 2. HTTP 연결 설정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // 핵심 수정: EUC-KR로 응답 읽기 (서울시 API가 종종 이 인코딩을 사용함)
        // EUC-KR과 UTF-8 공용으로 사용하네요 다시 찾아보니!
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8") // ★ 여기만 바꾸면 됨!
        );
        // 직접Json 파일 받아봐서 EUC-KR로 읽어오는 거 인코딩 상태 확인했으면
        // AI가 알려주는데에만 의지하고서 코드 작성해서 한글 깨지는 상태는 안 왔음

        // 3. 응답 읽기
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // 4. 연결 종료
        reader.close();
        conn.disconnect();

        // 5. JSON 응답 로그 출력 (디버깅용)
        String json = sb.toString();
        System.out.println(json.substring(0, 1000));
        System.out.println("Json 내용 받아온 뒤 1,000글자 출력 완료");

        return json;
    }
}
