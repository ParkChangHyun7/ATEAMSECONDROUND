//ParkingController.java
//클라이언트에서 "/api/parking"으로 요청 시 주차장 JSON 데이터를 응답해주는 Controller

package seoul.its.info.services.traffic.parking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seoul.its.info.services.traffic.parking.dto.ParkingDto;
import seoul.its.info.services.traffic.parking.service.ParkingService;

import java.util.List;

@RestController //REST 방식으로 데이터를 응답하는 컨트롤러임을 명시
@RequestMapping("/api") //공통 URI Prefix
public class ParkingController {

    private final ParkingService parkingService;

    @Autowired //의존성 주입
    public ParkingController(ParkingService parkingService){
        this.parkingService = parkingService;
    }

    @GetMapping("/parking")  //GET방식으로 요청시 동작 (예: /api/parking)
    public List<ParkingDto>getAllParkingLots() {
        return parkingService.getAllParkingLots(); //서비스에서 주차장 데이터 받아 응답
    }
}