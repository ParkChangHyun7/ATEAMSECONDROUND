package seoul.its.info.services.traffic.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class ParkingRestController {

    @GetMapping("/parking")
    public ResponseEntity<String> getParkingList() {
        try {
            // 현재 프로젝트 기준으로 직접 경로 설정 (수정 가능)
            Path path = Paths.get("src/main/data/api/json/parking/publicparking.json");

            //  파일 내용 읽기
            String json = Files.readString(path);

            //  JSON 응답 반환
            return ResponseEntity.ok(json);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"데이터를 불러오는 데 실패했습니다.\"}");
        }
    }
}