package seoul.its.info.services.traffic.parking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import seoul.its.info.services.traffic.parking.dto.PublicParkingDto;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ParkingService 인터페이스의 구현 클래스
 * JSON파일을 읽어서 주차장 정보를 파싱하고,DTO리스트로 반환
 */
@Service
public class ParkingServiceImpl implements ParkingService {

    @Override
    public List<PublicParkingDto> getAllPublicParking() {
        List<PublicParkingDto> result = new ArrayList<>();

        try {
            // JSON 파일 경로 설정
            File file = Paths.get("src/main/data/api/json/seoulpublicdata.json").toFile();
            ObjectMapper mapper = new ObjectMapper();

            // 루트 노드 파싱
            JsonNode root = mapper.readTree(file);

            // 데이터 배열 추출(key: DATA)
            JsonNode rows = root.get("DATA");

            if (rows != null) {
                for (JsonNode node : rows) {
                    String name = node.get("PARKING_NAME").asText(); // 주차장명
                    String addr = node.get("ADOR").asText(); // 주소
                    double lat = node.get("LAT").asDouble(); // 위도
                    double lng = node.get("LNG").asDouble(); // 경도

                    result.add(new PublicParkingDto(name, "공영", addr, lat, lng));
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // 에러 출력
        }

        return result;
    }

}