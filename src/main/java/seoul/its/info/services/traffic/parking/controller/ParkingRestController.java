package seoul.its.info.services.traffic.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seoul.its.info.services.traffic.parking.service.PublicParkingApiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParkingRestController {

    //  주차장 API 호출을 담당하는 서비스 의존성 주입
    private final PublicParkingApiService publicParkingApiService;

    /**
     *  실시간 API를 통해 서울시 공영/민영 주차장 JSON 데이터를 반환하는 엔드포인트
     * GET http://localhost:9998/api/parking
     */
    @GetMapping(value = "/parking", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> getParkingList() {
        try {
            //  API 서비스로부터 JSON 문자열 받아오기
            String json = publicParkingApiService.getParkingData();

            //  성공 응답 반환 (HTTP 200)
            return ResponseEntity.ok(json);

        } catch (Exception e) {
            e.printStackTrace();

            //  예외 발생 시 500 반환
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\" 데이터를 불러오는 데 실패했습니다.\"}");
        }
    }
}
