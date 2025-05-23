package seoul.its.info.services.traffic.parking.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seoul.its.info.services.traffic.parking.dto.PublicParkingDto;
import seoul.its.info.services.traffic.parking.service.ParkingService;

import java.util.List;


/**
 * JSON 데이터 제공용 RestController
 * -클라이언트(Vue, JS등)에서 fetch, axios 요청 보낼 때 사용하는 API전용 컨트롤러
 * -화면(JSP)을 띄우는 역할은 하지 않음
 */

 @RestController
 @RequestMapping("/api") //모든 요청의 앞부분은 /api로 시작
 public class ParkingRestController{

    private final ParkingService parkingService;

    //생성자 주입 방식 사용
    public ParkingRestController(ParkingService parkingService){
        this.parkingService = parkingService;
    }

    /**
     * GET요청: /api/parking
     * -전체 공영 주차장 데이터를 JSON 배열 형태로 응답
     * -fetch()로 지도에 마커 찍을 때 이 데이터를 사용함
     * 
     * @return 주차장 목록 List<PublicParkingDto>
     */
     @GetMapping("/parking")
     public List<PublicParkingDto>getAllParkingLots(){
        return parkingService.getAllPublicParking();
     }
 }
