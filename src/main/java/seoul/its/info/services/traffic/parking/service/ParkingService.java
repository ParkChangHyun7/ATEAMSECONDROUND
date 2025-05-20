//ParkingService.java
//주차장 정보를 가져오는 기능의 인터페이스
package seoul.its.info.services.traffic.parking.service;

import java.util.List;
import seoul.its.info.services.traffic.parking.dto.ParkingDto;


public interface ParkingService {

    //주차장 목록을 전체 조회하는 메서드
    List<ParkingDto>getAllParkingLots();
}
